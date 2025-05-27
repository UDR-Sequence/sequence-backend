package sequence.sequence_member.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sequence.sequence_member.member.service.EmailAuthService;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    // 이메일 인증 요청
    @PostMapping("/request")
    public ResponseEntity<String> requestEmailVerification(@RequestParam String email) {
        emailAuthService.requestEmailVerification(email);
        return ResponseEntity.ok("이메일로 인증 코드가 전송되었습니다.");
    }

    // 인증 코드 확인
    @PostMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestParam String email, @RequestParam String token) {
        emailAuthService.verifyEmailToken(email, token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }
}

