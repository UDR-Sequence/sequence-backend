package sequence.sequence_member.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sequence.sequence_member.member.dto.FindPasswordInputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FindPasswordService {
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String findPassword(FindPasswordInputDTO input) {
        // 사용자 검증
        Optional<MemberEntity> memberOptional = memberRepository.findByUsernameAndEmail(
                input.getUsername(), input.getEmail());

        if (memberOptional.isEmpty()) {
            return null;
        }

        MemberEntity member = memberOptional.get();

        // 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();

        // 비밀번호 업데이트
        member.setPassword(passwordEncoder.encode(temporaryPassword));
        memberRepository.save(member);

        // 이메일 발송
        sendPasswordResetEmail(member.getEmail(), temporaryPassword);

        return temporaryPassword;
    }

    private String generateTemporaryPassword() {
        //임시 비밀번호 생성 (12자리: 영문 대소문자, 숫자, 특수문자 포함)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    private void sendPasswordResetEmail(String email, String temporaryPassword) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("[Sequence] 임시 비밀번호 발급 안내");

            String content = "<html><body>"
                    + "<h2>임시 비밀번호 발급 안내</h2>"
                    + "<p>안녕하세요. 임시 비밀번호가 발급되었습니다.</p>"
                    + "<p>아래의 임시 비밀번호로 로그인 후, 보안을 위해 마이페이지에서 비밀번호를 변경해주세요.</p>"
                    + "<p><strong>임시 비밀번호: " + temporaryPassword + "</strong></p>"
                    + "</body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.",e);
        }
    }
}
