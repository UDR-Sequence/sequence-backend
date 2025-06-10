package sequence.sequence_member.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.exception.BaseException;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.FindPasswordInputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FindPasswordService {
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${NAVER_MAIL_USERNAME:dev_mj_@naver.com}")
    private String fromEmail;

    @Transactional
    public String findPassword(FindPasswordInputDTO input) {
        // 사용자 검증
        Optional<MemberEntity> memberOptional = memberRepository.findByUsernameAndEmail(
                input.getUsername(), input.getEmail());

        if (memberOptional.isEmpty()) {
            throw new BaseException(Code.BAD_REQUEST, "입력한 개인 정보와 일치하지 않습니다.");
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

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[Sequence] 임시 비밀번호 발급 안내");

            String content = "<!DOCTYPE html>" +
                    "<html lang=\"ko\">" +
                    "<head>" +
                    "<meta charset=\"UTF-8\">" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "<title>Sequence 임시비밀번호 발송</title>" +
                    "</head>" +
                    "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #151515; color: #ffffff;\">" +
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"background-color: #151515;\">" +
                    "<tr>" +
                    "<td align=\"center\" style=\"padding: 20px;\">" +
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\" style=\"max-width: 600px; background-color: #151515;\">" +

                    // 컨텐츠 섹션
                    "<tr>" +
                    "<td style=\"padding: 30px;\">" +
                    "<p style=\"font-size: 18px; color: #ffffff; margin: 0 0 20px 0;\">안녕하세요. 임시 비밀번호가 발급되었습니다.</p>" +
                    "<p style=\"font-size: 16px; color: #cccccc; margin: 0 0 30px 0; line-height: 1.6;\">아래의 임시 비밀번호로 로그인 후, 보안을 위해 마이페이지에서 비밀번호를 변경해주세요.</p>" +

                    // 비밀번호 섹션
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 30px 0;\">" +
                    "<tr>" +
                    "<td align=\"center\" style=\"background-color: #1a1a1a; border: 2px solid #E32929; border-radius: 4px; padding: 30px;\">" +
                    "<p style=\"color: #E32929; font-size: 12px; margin: 0 0 15px 0; text-transform: uppercase; letter-spacing: 2px; font-weight: 600;\">임시 비밀번호</p>" +
                    "<div style=\"background-color: #ffffff; color: #151515; font-size: 28px; font-weight: 900; padding: 15px 25px; border-radius: 4px; letter-spacing: 2px; font-family: 'Courier New', monospace; display: inline-block; margin: 0;\">" + temporaryPassword + "</div>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    // 경고 섹션
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 30px 0;\">" +
                    "<tr>" +
                    "<td style=\"background-color: #1a1a1a; padding: 25px;\">" +
                    "<h3 style=\"color: #E32929; font-size: 16px; font-weight: 700; margin: 0 0 15px 0; text-transform: uppercase;\">보안 알림 및 이용 안내</h3>" +
                    "<p style=\"color: #cccccc; font-size: 14px; margin: 5px 0; line-height: 1.6;\">• 로그인 즉시 마이페이지에서 새로운 비밀번호로 변경하세요</p>" +
                    "<p style=\"color: #cccccc; font-size: 14px; margin: 5px 0; line-height: 1.6;\">• 비밀번호를 타인과 공유하지 마세요</p>" +
                    "<p style=\"color: #cccccc; font-size: 14px; margin: 5px 0; line-height: 1.6;\">• Sequence 로그인 페이지에 접속합니다</p>" +
                    "<p style=\"color: #cccccc; font-size: 14px; margin: 5px 0; line-height: 1.6;\">• 이메일과 임시 비밀번호를 입력합니다</p>" +
                    "<p style=\"color: #cccccc; font-size: 14px; margin: 5px 0; line-height: 1.6;\">• 로그인 후 마이페이지에서 비밀번호 변경을 완료합니다</p>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +

                    // 구분선
                    "<tr>" +
                    "<td style=\"padding: 0 30px;\">" +
                    "<div style=\"height: 1px; background-color: #E32929; margin: 30px 0;\"></div>" +
                    "</td>" +
                    "</tr>" +

                    // 푸터 섹션
                    "<tr>" +
                    "<td align=\"center\" style=\"background-color: #0f0f0f; padding: 30px; border-top: 1px solid #333;\">" +
                    "<h2 style=\"font-size: 24px; font-weight: 900; color: #E32929; margin: 0 0 15px 0;\">Sequence</h2>" +
                    "<p style=\"color: #888888; font-size: 13px; margin: 5px 0;\">이 메시지는 발신 전용입니다.</p>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.",e);
        }
    }
}
