package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.minio.service.MinioService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveFileService {
    private final MinioService minioService;

    @Value("${MINIO_ARCHIVE_IMG}")
    private String ARCHIVE_IMG_BUCKET;

    @Value("${MINIO_ARCHIVE_THUMBNAIL}")
    private String ARCHIVE_THUMBNAIL_BUCKET;

    public String uploadThumbnail(Long archiveId, MultipartFile thumbnailFile) throws Exception {
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            return null;
        }
        validateImageFile(thumbnailFile);
        String fileName = generateThumbnailFileName(archiveId, thumbnailFile);
        return minioService.uploadFileMinio(ARCHIVE_THUMBNAIL_BUCKET, fileName, thumbnailFile);
    }

    public List<String> uploadImages(Long archiveId, List<MultipartFile> imageFiles) throws Exception {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : imageFiles) {
            if (isValidImageFile(file)) {
                String url = uploadSingleImage(archiveId, file);
                uploadedUrls.add(url);
            }
        }
        return uploadedUrls;
    }

    private String uploadSingleImage(Long archiveId, MultipartFile imageFile) throws Exception {
        String fileName = generateArchiveFileName(archiveId, imageFile);
        return minioService.uploadFileMinio(ARCHIVE_IMG_BUCKET, fileName, imageFile);
    }

    private void validateImageFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BAD_REQUEST_EXCEPTION("파일이 비어있습니다.");
        }
        if (!isValidImageFile(file)) {
            throw new BAD_REQUEST_EXCEPTION("이미지 파일만 업로드 가능합니다.");
        }
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }
        return file.getSize() <= 5 * 1024 * 1024; // 5MB 제한
    }

    private String generateArchiveFileName(Long archiveId, MultipartFile file) {
        return String.format("archive_%d_%d_%s", 
            archiveId, 
            System.currentTimeMillis(), 
            file.getOriginalFilename());
    }

    private String generateThumbnailFileName(Long archiveId, MultipartFile file) {
        return String.format("thumbnail_%d_%d_%s", 
            archiveId, 
            System.currentTimeMillis(), 
            file.getOriginalFilename());
    }
} 