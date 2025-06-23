package sequence.sequence_member.global.minio.service;


import io.minio.*;
import io.minio.http.Method;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.annotation.MethodDescription;
import sequence.sequence_member.global.exception.MinioException;
import sequence.sequence_member.global.utils.FileExtension;
import sequence.sequence_member.global.utils.MultipartUtil;
import lombok.Getter;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private final FileExtension fileExtension;
    private final MinioClient minioClient;

    @MethodDescription(description = "minio 서버에 파일 업로드")
    public String uploadFileMinio(String bucketName, String fileName, MultipartFile file) throws Exception {

        // 업로드 진행 전 파일 확장자 한번 더 확인
        uploadFileCheck(file);

        //해당 버킷이 존재하지 않는 경우 버킷을 새로 생성
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if(!found){
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
        }

        //이미지 업로드
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        //해당 이미지 url 리턴
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .build());

    }

    @MethodDescription(description = "파일의 확장자를 확인합니다.")
    public void uploadFileCheck(MultipartFile file) {
        String extension = fileExtension.getFileExtension(file);
        fileExtension.uploadFileExtensionCheck(extension);
    }



    @MethodDescription(description = "파일 이름과 버킷 이름을 통해 파일을 다운로드 받습니다.")
    public ResponseEntity<byte[]> downloadFile(String fileName, String bucketName) throws Exception {
        InputStream fileData = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
        byte[] bytes = IOUtils.toByteArray(fileData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        log.info("[+] HttpHeaders = [{}] ", headers);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @MethodDescription(description = "파일 이름과 버킷 이름을 통해 파일을 삭제합니다.")
    public void deleteFile(String bucketName, String fileName) {
        if (bucketName == null || fileName == null || bucketName.isEmpty() || fileName.isEmpty()) {
            return;
        }
        
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}/{} - {}", bucketName, fileName, e.getMessage());
        }
    }

    @MethodDescription(description = "URL을 통해 파일을 삭제합니다.")
    public void deleteFileByUrl(String fileUrl) {
        String baseUrl = fileUrl.split("\\?")[0];
        java.net.URL parsedUrl;
        
        try {
            parsedUrl = new java.net.URL(baseUrl);
            String path = parsedUrl.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            int slashIndex = path.indexOf('/');
            if (slashIndex != -1) {
                String bucketName = path.substring(0, slashIndex);
                String fileName = path.substring(slashIndex + 1);
                deleteFile(bucketName, fileName);
            }
        } catch (Exception ignored) {
            // 예외 무시하고 진행
        }
    }
}
