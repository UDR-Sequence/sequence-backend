package sequence.sequence_member.member.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import sequence.sequence_member.member.entity.EmailAuthTokenEntity;
import sequence.sequence_member.member.repository.EmailAuthTokenRepository;

@RequiredArgsConstructor
@Service
public class EmailAuthService {

    private final JavaMailSender mailSender;
    private final EmailAuthTokenRepository tokenRepo;
    private final SpringTemplateEngine templateEngine;

    @Value("${NAVER_MAIL_USERNAME:dev_mj_@naver.com}")
    private String fromEmail;

    public void requestEmailVerification(String email) {
        tokenRepo.findAllByEmail(email).forEach(token -> {
            token.setExpired(true);
            tokenRepo.save(token);
        });

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        EmailAuthTokenEntity emailAuthToken = EmailAuthTokenEntity.builder()
                .email(email)
                .token(token)
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .build();

        tokenRepo.save(emailAuthToken);
        sendAuthEmail(email, token);
    }

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

        tokenRepo.findAllByEmail(email).forEach(t -> {
            t.setVerified(false);
            tokenRepo.save(t);
        });

        authToken.setVerified(true);
        tokenRepo.save(authToken);
    }

    private void sendAuthEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("token", token);

            String htmlContent = templateEngine.process("emailAuth", context);

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[Sequence] 이메일 인증 안내");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }
}
