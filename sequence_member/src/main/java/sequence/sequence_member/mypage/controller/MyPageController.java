package sequence.sequence_member.mypage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.mypage.dto.MyPageRequestDto;
import sequence.sequence_member.mypage.dto.MyPageResponseDto;
import sequence.sequence_member.mypage.service.MyPageService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/api/mypage")
    public ResponseEntity<ApiResponseData> getMyProfile(
            @RequestParam(defaultValue = "0") int page,  // 페이지 기본값 0
            @RequestParam(defaultValue = "10") int size  // 사이즈 기본값 10
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            MyPageResponseDto myPageDTO = myPageService.getMyProfile(username, page, size);
            // 성공 응답 생성
            return ResponseEntity.ok(ApiResponseData.success(myPageDTO, "사용자 정보를 성공적으로 가져왔습니다."));
        } catch (Exception e) {
            // 예외 발생 시 처리
            ApiResponseData errorResponse = ApiResponseData.failure(
                    Code.CAN_NOT_FIND_RESOURCE.getCode(),
                    e.getMessage()
            );
            return ResponseEntity.status(Code.CAN_NOT_FIND_RESOURCE.getStatus()).body(errorResponse);
        }
    }

    @PutMapping(value = "/api/mypage", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseData<String>> updateMyProfile(
            @RequestPart(name = "myPageDTO") MyPageRequestDto myPageDTO,
            @RequestPart(name = "authImgFile", required = false) MultipartFile authImgFile,
            @RequestPart(name = "portfolios", required = false) List<MultipartFile> portfolios
    ) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            myPageService.updateMyProfile(myPageDTO, username, authImgFile, portfolios);
            return ResponseEntity.ok(ApiResponseData.success("마이페이지 수정이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(Code.CAN_NOT_FIND_RESOURCE.getStatus())
                    .body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "예외가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/api/mypage/{nickname}")
    public ResponseEntity<ApiResponseData> getUserProfile(
            @PathVariable String nickname,
            @RequestParam(defaultValue = "0") int page,  // 페이지 기본값 0
            @RequestParam(defaultValue = "10") int size  // 사이즈 기본값 10
    ) {
        try {
            MyPageResponseDto userProfile = myPageService.getUserProfile(nickname, page, size);
            return ResponseEntity.ok(ApiResponseData.success(userProfile, nickname + "님의 정보를 성공적으로 가져왔습니다."));
        } catch (Exception e) {
            ApiResponseData errorResponse = ApiResponseData.failure(
                    Code.CAN_NOT_FIND_RESOURCE.getCode(),
                    e.getMessage()
            );
            return ResponseEntity.status(Code.CAN_NOT_FIND_RESOURCE.getStatus()).body(errorResponse);
        }
    }
}
