package sequence.sequence_member.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.mypage.dto.MyPageRequestDTO;
import sequence.sequence_member.mypage.dto.MyPageResponseDTO;
import sequence.sequence_member.mypage.dto.UpdateLoginInfoRequestDTO;
import sequence.sequence_member.mypage.service.MyPageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/api/mypage")
    public ResponseEntity<ApiResponseData<MyPageResponseDTO>> getMyProfile(
            @RequestParam(defaultValue = "0") int page,  // 페이지 기본값 0
            @RequestParam(defaultValue = "10") int size,  // 사이즈 기본값 10
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        String username = customUserDetails.getUsername();

        try {
            MyPageResponseDTO myPageDTO = myPageService.getMyProfile(username, page, size, customUserDetails);
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
            @RequestPart(name = "myPageDTO") MyPageRequestDTO myPageDTO,
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
    public ResponseEntity<ApiResponseData<MyPageResponseDTO>> getUserProfile(
            @PathVariable String nickname,
            @RequestParam(defaultValue = "0") int page,  // 페이지 기본값 0
            @RequestParam(defaultValue = "10") int size,   // 사이즈 기본값 10
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            MyPageResponseDTO userProfile = myPageService.getUserProfile(nickname, page, size, customUserDetails);
            return ResponseEntity.ok(ApiResponseData.success(userProfile, nickname + "님의 정보를 성공적으로 가져왔습니다."));
        } catch (Exception e) {
            ApiResponseData errorResponse = ApiResponseData.failure(
                    Code.CAN_NOT_FIND_RESOURCE.getCode(),
                    e.getMessage()
            );
            return ResponseEntity.status(Code.CAN_NOT_FIND_RESOURCE.getStatus()).body(errorResponse);
        }
    }

    @PostMapping("/api/mypage/update")
    public ResponseEntity<ApiResponseData<String>> updateUserInfo(
            @Valid @RequestBody UpdateLoginInfoRequestDTO updateLoginInfoRequestDTO, Errors errors
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        myPageService.updateUserInfo(updateLoginInfoRequestDTO, username, errors);

        if (!updateLoginInfoRequestDTO.isPasswordMatching()) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseData.failure(
                            Code.CAN_NOT_FIND_RESOURCE.getCode(),
                            "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
                    );
        }

        return ResponseEntity.ok(ApiResponseData.success("로그인 정보 수정이 완료되었습니다."));
    }
}
