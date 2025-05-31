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

    // 이메일 요청: 기존 토큰 만료 → 새 토큰 생성 + 메일 전송 + 저장
    public void requestEmailVerification(String email) {
        // 기존 토큰 모두 만료 처리
        tokenRepo.findAllByEmail(email).forEach(token -> {
            token.setExpired(true);
            tokenRepo.save(token);
        });

        // 새 토큰 발급
        String token = UUID.randomUUID().toString().substring(0, 6);

        EmailAuthTokenEntity emailAuthToken = EmailAuthTokenEntity.builder()
                .email(email)
                .token(token)
                .createdAt(LocalDateTime.now())
                .isVerified(false)
                .build();

        tokenRepo.save(emailAuthToken);

        // 메일 전송
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
}

