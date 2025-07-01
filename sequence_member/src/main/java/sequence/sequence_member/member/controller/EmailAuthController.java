package sequence.sequence_member.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.EmailAuthInputDTO;
import sequence.sequence_member.member.service.EmailAuthService;

@Slf4j
@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    // 이메일 인증 요청
    @PostMapping("/request")
    public ResponseEntity<ApiResponseData<String>> requestEmailVerification(@RequestBody EmailAuthInputDTO request) {
        log.info("이메일 인증 요청 : /api/auth/email/request POST request 발생");

        emailAuthService.requestEmailVerification(request.getEmail());
        return ResponseEntity.ok().body(ApiResponseData.success(request.getEmail(), "이메일로 인증 코드가 전송되었습니다."));
    }

    // 인증 코드 확인
    @PostMapping("/verify")
    public ResponseEntity<ApiResponseData<String>> verifyToken(@RequestBody EmailAuthInputDTO request) {
        log.info("인증코드 확인 요청 : /api/auth/email/verify POST request 발생");

        emailAuthService.verifyEmailToken(request.getEmail(), request.getToken());
        return ResponseEntity.ok().body(ApiResponseData.success(request.getEmail(), "이메일 인증이 완료되었습니다."));
    }
}

