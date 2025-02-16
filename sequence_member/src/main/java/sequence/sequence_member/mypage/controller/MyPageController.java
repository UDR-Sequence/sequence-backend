package sequence.sequence_member.mypage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.mypage.dto.MyPageDTO;
import sequence.sequence_member.mypage.service.MyPageService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {
    private final MyPageService myPageService;
    private final JWTUtil jwtUtil;

    @GetMapping("/api/mypage")
    public ResponseEntity<ApiResponseData> getMyProfile(HttpServletRequest request) {

        String username = jwtUtil.getUsername(request.getHeader("access"));

        try {
            MyPageDTO myPageDTO = myPageService.getMyProfile(username);
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

    @PutMapping("/api/mypage")
    public ResponseEntity<ApiResponseData<String>> updateMyProfile(@RequestBody MyPageDTO myPageDTO) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            myPageService.updateMyProfile(myPageDTO, username);
            return ResponseEntity.ok(ApiResponseData.success("마이페이지 수정이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(Code.CAN_NOT_FIND_RESOURCE.getStatus())
                    .body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "예외가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/api/mypage/{nickname}")
    public ResponseEntity<ApiResponseData> getUserProfile(@PathVariable String nickname) {
        try {
            MyPageDTO userProfile = myPageService.getUserProfile(nickname);
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
