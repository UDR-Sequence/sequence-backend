package sequence.sequence_member.member.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sequence.sequence_member.member.entity.EmailAuthTokenEntity;
import sequence.sequence_member.member.repository.EmailAuthTokenRepository;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final EmailAuthTokenRepository tokenRepo;

    // 이메일 요청: 토큰 생성 + 메일 전송 + 저장
    public void requestEmailVerification(String email) {
        String token = UUID.randomUUID().toString().substring(0, 6); // 6자리 토큰
        EmailAuthTokenEntity<Object> emailAuthToken = EmailAuthTokenEntity.builder()
                .email(email)
                .token(token)
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .build();

        tokenRepo.save(emailAuthToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("아래 인증 코드를 입력해주세요: " + token);

        mailSender.send(message);
    }

    // 인증 확인
    public void verifyEmailToken(String email, String token) {
        EmailAuthTokenEntity authToken = tokenRepo.findByEmailAndToken(email, token)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 정보입니다."));

        if (authToken.isExpired()) {
            throw new IllegalStateException("토큰이 만료되었습니다.");
        }

        authToken.setVerified(true);
        tokenRepo.save(authToken);
    }
}

