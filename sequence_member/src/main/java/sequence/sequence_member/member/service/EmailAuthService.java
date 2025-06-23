package sequence.sequence_member.member.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import sequence.sequence_member.member.entity.EmailAuthTokenEntity;
import sequence.sequence_member.member.repository.EmailAuthTokenRepository;
import org.springframework.beans.factory.annotation.Value;


@RequiredArgsConstructor
@Service
public class EmailAuthService {

    private final JavaMailSender mailSender;

    @Value("${NAVER_MAIL_USERNAME:dev_mj_@naver.com}")
    private String fromEmail;
    private final EmailAuthTokenRepository tokenRepo;

    public void requestEmailVerification(String email) {
        tokenRepo.findAllByEmail(email).forEach(token -> {
            token.setExpired(true);
            tokenRepo.save(token);
        });

        String token = UUID.randomUUID().toString().substring(0, 6);

        EmailAuthTokenEntity emailAuthToken = EmailAuthTokenEntity.builder()
                .email(email)
                .token(token)
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .build();

        tokenRepo.save(emailAuthToken);

        sendAuthEmail(email, token);
    }

    // 인증 확인
    public void verifyEmailToken(String email, String token) {
        EmailAuthTokenEntity authToken = tokenRepo.findByEmailAndToken(email, token)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 인증 정보입니다."));

        if (authToken.isExpired()) {
            throw new IllegalStateException("이미 토큰이 만료되었습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (authToken.getCreatedAt().plusMinutes(5).isBefore(now)) {
            authToken.setExpired(true);
            tokenRepo.save(authToken);
            throw new IllegalStateException("시간 초과로 인하여 토큰이 만료되었습니다.");
        }

        // 해당 이메일의 모든 토큰 만료 처리
        tokenRepo.findAllByEmail(email).forEach(t -> {
            t.setVerified(false); // 기존 것들 다 false로 초기화
            tokenRepo.save(t);
        });

        // 현재 토큰만 인증 처리
        authToken.setVerified(true);
        tokenRepo.save(authToken);
    }


    
    // MimeMessage 방식으로 인증 메일 전송
    private void sendAuthEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[Sequence] 이메일 인증 코드");

            String content = "<html><body>"
                    + "<h2>이메일 인증 안내</h2>"
                    + "<p>아래의 인증 코드를 입력해주세요.</p>"
                    + "<p><strong>인증 코드: " + token + "</strong></p>"
                    + "</body></html>";

            helper.setText(content, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
    
}

