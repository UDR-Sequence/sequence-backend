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
                    "<style>" +
                    "@media only screen and (max-width: 600px) {" +
                    ".container { width: 100% !important; max-width: 100% !important; }" +
                    ".mobile-padding { padding: 20px !important; }" +
                    ".mobile-text { font-size: 16px !important; }" +
                    ".mobile-title { font-size: 18px !important; }" +
                    ".mobile-password { font-size: 22px !important; padding: 12px 20px !important; }" +
                    ".mobile-center { text-align: center !important; }" +
                    ".mobile-hide { display: none !important; }" +
                    "}" +
                    "@media only screen and (max-width: 480px) {" +
                    ".mobile-padding { padding: 15px !important; }" +
                    ".mobile-text { font-size: 14px !important; }" +
                    ".mobile-title { font-size: 16px !important; }" +
                    ".mobile-password { font-size: 20px !important; padding: 10px 15px !important; }" +
                    "}" +
                    "</style>" +
                    "</head>" +
                    "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #151515; color: #ffffff; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%;\">" +

                    // 외부 컨테이너
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"background-color: #151515; min-height: 100vh;\">" +
                    "<tr>" +
                    "<td align=\"center\" style=\"padding: 10px;\">" +

                    // 메인 컨테이너 (반응형)
                    "<table class=\"container\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\" style=\"max-width: 600px; width: 100%; background-color: #151515; margin: 0 auto;\">" +

                    // 헤더 섹션
                    "<tr>" +
                    "<td class=\"mobile-padding\" align=\"center\" style=\"background-color: #0f0f0f; padding: 40px 30px; border-bottom: 2px solid #E32929;\">" +
                    "<h1 style=\"font-size: 32px; font-weight: 900; color: #E32929; margin: 0 0 15px 0; font-family: Arial, sans-serif; line-height: 1.2;\">Sequence</h1>" +
                    "<h2 class=\"mobile-title\" style=\"font-size: 24px; color: #ffffff; margin: 0 0 10px 0; font-weight: 700; font-family: Arial, sans-serif; line-height: 1.3;\">🔑 임시 비밀번호 발급</h2>" +
                    "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 16px; margin: 0; font-family: Arial, sans-serif; line-height: 1.4;\">계정 복구를 위한 임시 비밀번호를 발급했습니다</p>" +
                    "</td>" +
                    "</tr>" +

                    // 컨텐츠 섹션
                    "<tr>" +
                    "<td class=\"mobile-padding\" style=\"padding: 40px 30px; background-color: #151515;\">" +

                    // 안내 메시지
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 25px;\">" +
                    "<tr>" +
                    "<td style=\"padding: 0;\">" +
                    "<p class=\"mobile-text\" style=\"font-size: 18px; color: #ffffff; margin: 0 0 15px 0; font-family: Arial, sans-serif; line-height: 1.5;\">" +
                    "안녕하세요," +
                    "</p>" +
                    "<p class=\"mobile-text\" style=\"font-size: 16px; color: #cccccc; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">" +
                    "요청하신 임시 비밀번호가 발급되었습니다. 아래의 임시 비밀번호로 로그인 후, 보안을 위해 마이페이지에서 비밀번호를 변경해주세요." +
                    "</p>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    // 임시 비밀번호 섹션
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 30px 0;\">" +
                    "<tr>" +
                    "<td style=\"background-color: #1a1a1a; border: 2px solid #E32929; border-radius: 8px; padding: 25px 20px;\">" +

                    // 비밀번호 라벨
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 15px;\">" +
                    "<tr>" +
                    "<td align=\"center\">" +
                    "<p style=\"color: #E32929; font-size: 12px; margin: 0; text-transform: uppercase; letter-spacing: 2px; font-weight: 600; font-family: Arial, sans-serif;\">임시 비밀번호</p>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    // 비밀번호 표시
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">" +
                    "<tr>" +
                    "<td align=\"center\">" +
                    "<div class=\"mobile-password\" style=\"background-color: #ffffff; color: #151515; font-size: 28px; font-weight: 900; padding: 15px 25px; border-radius: 4px; letter-spacing: 2px; font-family: 'Courier New', monospace; display: inline-block; margin: 0; word-break: break-all; max-width: 100%; box-sizing: border-box;\">" +
                    temporaryPassword +
                    "</div>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    // 보안 안내 섹션
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 25px 0;\">" +
                    "<tr>" +
                    "<td style=\"background-color: #1a1a1a; padding: 20px; border-radius: 6px;\">" +
                    "<h3 class=\"mobile-title\" style=\"color: #E32929; font-size: 16px; font-weight: 700; margin: 0 0 15px 0; text-transform: uppercase; font-family: Arial, sans-serif;\">보안 알림 및 이용 안내</h3>" +

                    // 안내사항 목록
                    "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">" +
                    "<tr>" +
                    "<td style=\"vertical-align: top; padding-bottom: 5px;\">" +
                    "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 로그인 즉시 마이페이지에서 새로운 비밀번호로 변경하세요</p>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"vertical-align: top; padding-bottom: 5px;\">" +
                    "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 비밀번호를 타인과 공유하지 마세요</p>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"vertical-align: top; padding-bottom: 5px;\">" +
                    "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">• Sequence 로그인 페이지에 접속합니다</p>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"vertical-align: top; padding-bottom: 5px;\">" +
                    "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 이메일과 임시 비밀번호를 입력합니다</p>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"vertical-align: top;\">" +
                    "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 로그인 후 마이페이지에서 비밀번호 변경을 완료합니다</p>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    "</td>" +
                    "</tr>" +

                    // 구분선
                    "<tr>" +
                    "<td style=\"padding: 0 30px;\">" +
                    "<div style=\"height: 1px; background-color: #E32929; margin: 20px 0;\"></div>" +
                    "</td>" +
                    "</tr>" +

                    // 푸터 섹션
                    "<tr>" +
                    "<td class=\"mobile-padding mobile-center\" align=\"center\" style=\"background-color: #0f0f0f; padding: 30px; border-top: 1px solid #333;\">" +
                    "<h2 style=\"font-size: 24px; font-weight: 900; color: #E32929; margin: 0 0 15px 0; font-family: Arial, sans-serif;\">Sequence</h2>" +
                    "<p class=\"mobile-text\" style=\"color: #888888; font-size: 13px; margin: 5px 0; font-family: Arial, sans-serif;\">이 메시지는 발신 전용입니다.</p>" +
                    "<p class=\"mobile-text\" style=\"color: #888888; font-size: 13px; margin: 5px 0; font-family: Arial, sans-serif;\">Sequence © 2025</p>" +
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
