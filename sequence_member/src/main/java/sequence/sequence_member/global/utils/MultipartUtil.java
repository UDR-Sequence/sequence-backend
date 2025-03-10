package sequence.sequence_member.global.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.annotation.MethodDescription;
import sequence.sequence_member.global.exception.BaseException;
import sequence.sequence_member.global.exception.MultipartException;
import sequence.sequence_member.global.minio.service.MinioService;

@Component
@RequiredArgsConstructor
public class MultipartUtil{

    private final FileExtension fileExtension;
    private final MinioService minioService;

    @MethodDescription(description = "파일을 업로드 하고, 파일 이름을 반환받습니다.")
    private String uploadFile(MultipartFile file, String userName, String suffix, String bucketName) {
        String extension = fileExtension.getFileExtension(file);
        System.out.println("extension = " + extension + ", userName = " + userName);
        String fileName = generateFileName(userName, suffix, extension);
        String fileUrl;
        try{
            fileUrl = minioService.uploadFileMinio(bucketName, fileName, file);
        }catch(Exception e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage());
        }

        return fileUrl;
    }

    @MethodDescription(description = "지정된 형식으로 파일명 생성")
    public String generateFileName(String username, String suffix, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return username + "_" + now.format(formatter) + "_" + suffix + "." + extension;
    }

    @MethodDescription(description = "파일 이름을 결정합니다.")
    public String determineFileName(MultipartFile file, String userName, String suffix, String bucketName) {
        if (file == null || file.isEmpty()) {
            return "default.png";
        }
        return uploadFile(file, userName, suffix, bucketName);
    }
}

