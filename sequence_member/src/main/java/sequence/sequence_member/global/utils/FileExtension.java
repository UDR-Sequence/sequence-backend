package sequence.sequence_member.global.utils;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.annotation.MethodDescription;
import sequence.sequence_member.global.exception.MultipartException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public interface FileExtension {
    @MethodDescription(description = "파일 확장자 추출")
    String getFileExtension(MultipartFile file);

    @MethodDescription(description = "파일 확장자를 확인합니다.")
    void uploadFileExtensionCheck(String extension);
}
