package sequence.sequence_member.global.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.annotation.MethodDescription;
import sequence.sequence_member.global.exception.MultipartException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FileExtensionImpl implements FileExtension{
    private String[] extensions = {"png", "jpeg", "jpg", "bmp"};
    private List<String> allowedFileExtensions = new ArrayList<>(Arrays.asList(extensions));

    @Override
    @MethodDescription(description = "파일 확장자 추출")
    public String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.hasText(originalFileName)) {
            return originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
        }
        return "";
    }

    @Override
    @MethodDescription(description = "파일 확장자를 확인합니다.")
    public void uploadFileExtensionCheck(String extension) {
        if (!allowedFileExtensions.contains(extension)) {
            throw new MultipartException();
        }
    }
}
