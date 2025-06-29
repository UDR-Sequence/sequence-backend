package sequence.sequence_member.project.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.project.entity.Project;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectInviteEmailService {
    private final JavaMailSender mailSender;

    @Value("${NAVER_MAIL_USERNAME:dev_mj_@naver.com}")
    private String fromEmail;

    // 프로젝트 초대 이메일 발송 - 비동기
    @Async
    public void sendInviteEmail(Project project, MemberEntity invitedMember) {
        try {
            log.info("이메일 발송 시작 - 수신자: {}, 프로젝트: {}",
                    invitedMember.getEmail(), project.getProjectName());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 기본 설정
            helper.setFrom(fromEmail);
            helper.setTo(invitedMember.getEmail());
            helper.setSubject(String.format("[%s] 프로젝트 초대장이 도착했습니다!", project.getProjectName()));

            // HTML 이메일 내용 작성
            String htmlContent = createInvitationHtmlContent(project, invitedMember);
            helper.setText(htmlContent, true);

            // 이메일 발송
            mailSender.send(message);
            log.info("프로젝트 초대 이메일 발송 완료 - 수신자: {}, 프로젝트: {}",
                    invitedMember.getEmail(), project.getProjectName());

        } catch (MessagingException e) {
            log.error("이메일 발송 실패 - 수신자: {}, 프로젝트: {}, 오류: {}",
                    invitedMember.getEmail(), project.getProjectName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("예상치 못한 이메일 발송 오류 - 수신자: {}, 프로젝트: {}, 오류: {}",
                    invitedMember.getEmail(), project.getProjectName(), e.getMessage(), e);
        }
    }

    // 프로젝트 수정 알림 이메일 발송
    @Async
    public void sendProjectUpdateEmail(Project project, List<MemberEntity> members, String updateDetails) {
        if (project == null) {
            log.error("프로젝트 정보가 없습니다.");
            return;
        }

        if (members == null || members.isEmpty()) {
            log.warn("알림을 받을 멤버가 없습니다 - 프로젝트: {}", project.getProjectName());
            return;
        }

        for (MemberEntity member : members) {
            try {
                if (member == null || member.getEmail() == null || member.getEmail().trim().isEmpty()) {
                    log.warn("유효하지 않은 멤버 정보 - 프로젝트: {}", project.getProjectName());
                    continue;
                }

                log.info("프로젝트 수정 알림 이메일 발송 시작 - 수신자: {}, 프로젝트: {}",
                        member.getEmail(), project.getProjectName());

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(member.getEmail());
                helper.setSubject(String.format("[%s] 프로젝트 정보가 변경되었습니다",
                        project.getProjectName()));

                String htmlContent = createUpdateHtmlContent(project, member, updateDetails);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                log.info("프로젝트 수정 알림 이메일 발송 완료 - 수신자: {}, 프로젝트: {}",
                        member.getEmail(), project.getProjectName());

            } catch (MessagingException e) {
                log.error("프로젝트 수정 알림 이메일 발송 실패 - 수신자: {}, 프로젝트: {}, 오류: {}",
                        member.getEmail(), project.getProjectName(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("프로젝트 수정 알림 예상치 못한 오류 - 수신자: {}, 프로젝트: {}, 오류: {}",
                        member.getEmail(), project.getProjectName(), e.getMessage(), e);
            }
        }
    }

    // 프로젝트 초대 이메일 HTML 템플릿 (반응형)
    private String createInvitationHtmlContent(Project project, MemberEntity invitedMember) {
        // Null 체크 및 기본값 설정
        String nickname = invitedMember.getNickname() != null ? invitedMember.getNickname() : "회원";
        String writerNickname = project.getWriter() != null && project.getWriter().getNickname() != null
                ? project.getWriter().getNickname() : "관리자";
        String title = project.getTitle() != null ? project.getTitle() : "프로젝트";
        String projectName = project.getProjectName() != null ? project.getProjectName() : "프로젝트";
        String category = project.getCategory() != null ? project.getCategory().toString() : "미정";
        int personnel = project.getPersonnel();
        String startDate = project.getStartDate() != null ? project.getStartDate().toString() : "미정";
        String endDate = project.getEndDate() != null ? project.getEndDate().toString() : "미정";

        return "<!DOCTYPE html>" +
                "<html lang=\"ko\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Sequence 프로젝트 초대</title>" +
                "<style>" +
                "@media only screen and (max-width: 600px) {" +
                ".container { width: 100% !important; max-width: 100% !important; }" +
                ".mobile-padding { padding: 20px !important; }" +
                ".mobile-text { font-size: 16px !important; }" +
                ".mobile-title { font-size: 18px !important; }" +
                ".mobile-hide { display: none !important; }" +
                ".mobile-center { text-align: center !important; }" +
                "}" +
                "@media only screen and (max-width: 480px) {" +
                ".mobile-padding { padding: 15px !important; }" +
                ".mobile-text { font-size: 14px !important; }" +
                ".mobile-title { font-size: 16px !important; }" +
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
                "<h2 class=\"mobile-title\" style=\"font-size: 24px; color: #ffffff; margin: 0 0 10px 0; font-weight: 700; font-family: Arial, sans-serif; line-height: 1.3;\">📋 프로젝트 초대 알림</h2>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 16px; margin: 0; font-family: Arial, sans-serif; line-height: 1.4;\">새로운 프로젝트 참여 요청이 있습니다</p>" +
                "</td>" +
                "</tr>" +

                // 컨텐츠 섹션
                "<tr>" +
                "<td class=\"mobile-padding\" style=\"padding: 40px 30px; background-color: #151515;\">" +

                // 인사말
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 25px;\">" +
                "<tr>" +
                "<td style=\"padding: 0;\">" +
                "<p class=\"mobile-text\" style=\"font-size: 18px; color: #ffffff; margin: 0; font-family: Arial, sans-serif; line-height: 1.5;\">" +
                "안녕하세요 <span style=\"color: #ffffff; background-color: #E32929; padding: 3px 10px; border-radius: 4px; font-weight: bold; white-space: nowrap;\">" + nickname + "</span>님," +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                // 초대 메시지
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 30px;\">" +
                "<tr>" +
                "<td style=\"padding: 0;\">" +
                "<p class=\"mobile-text\" style=\"font-size: 16px; color: #cccccc; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">" +
                "<strong style=\"color: #ffffff;\">" + writerNickname + "</strong>님이 회원님을 다음 프로젝트에 초대했습니다." +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                // 프로젝트 정보 섹션 (반응형 테이블)
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 25px 0;\">" +
                "<tr>" +
                "<td style=\"background-color: #1a1a1a; border: 2px solid #E32929; border-radius: 8px; padding: 25px 20px;\">" +

                // 프로젝트 제목
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 20px;\">" +
                "<tr>" +
                "<td>" +
                "<h3 class=\"mobile-title\" style=\"color: #E32929; font-size: 20px; margin: 0; font-weight: 700; font-family: Arial, sans-serif; line-height: 1.3; word-break: break-word;\">" +
                "📋 " + title +
                "</h3>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                // 프로젝트 상세 정보 (모바일에서 세로 정렬)
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">" +
                "<tr>" +
                "<td style=\"vertical-align: top; padding-bottom: 8px;\">" +
                "<p class=\"mobile-text\" style=\"margin: 0; color: #cccccc; font-size: 14px; line-height: 1.8; font-family: Arial, sans-serif;\">" +
                "<strong style=\"color: #ffffff; display: inline-block; min-width: 80px;\">프로젝트명:</strong> " + projectName +
                "</p>" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"vertical-align: top; padding-bottom: 8px;\">" +
                "<p class=\"mobile-text\" style=\"margin: 0; color: #cccccc; font-size: 14px; line-height: 1.8; font-family: Arial, sans-serif;\">" +
                "<strong style=\"color: #ffffff; display: inline-block; min-width: 80px;\">카테고리:</strong> " + category +
                "</p>" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"vertical-align: top; padding-bottom: 8px;\">" +
                "<p class=\"mobile-text\" style=\"margin: 0; color: #cccccc; font-size: 14px; line-height: 1.8; font-family: Arial, sans-serif;\">" +
                "<strong style=\"color: #ffffff; display: inline-block; min-width: 80px;\">모집인원:</strong> " + personnel + "명" +
                "</p>" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"vertical-align: top;\">" +
                "<p class=\"mobile-text\" style=\"margin: 0; color: #cccccc; font-size: 14px; line-height: 1.8; font-family: Arial, sans-serif;\">" +
                "<strong style=\"color: #ffffff; display: inline-block; min-width: 80px;\">프로젝트 기간:</strong> " + startDate + " ~ " + endDate +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                "</td>" +
                "</tr>" +
                "</table>" +

                // 안내 섹션
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 25px 0;\">" +
                "<tr>" +
                "<td style=\"background-color: #1a1a1a; padding: 20px; border-radius: 6px;\">" +
                "<h3 class=\"mobile-title\" style=\"color: #E32929; font-size: 16px; font-weight: 700; margin: 0 0 15px 0; font-family: Arial, sans-serif;\">💡 안내사항</h3>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 8px 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 프로젝트 참여 초대 안내</p>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 8px 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 참여 의사를 결정하시려면 로그인 후 확인해주세요</p>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 8px 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 추가 문의사항은 프로젝트 관리자에게 연락해주세요</p>" +
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
    }

    // 프로젝트 수정 알림 이메일 HTML 템플릿 (반응형)
    private String createUpdateHtmlContent(Project project, MemberEntity member, String updateDetails) {
        String nickname = member.getNickname() != null ? member.getNickname() : "회원";
        String projectName = project.getProjectName() != null ? project.getProjectName() : "프로젝트";
        String details = updateDetails != null && !updateDetails.isEmpty() ? updateDetails : "프로젝트 정보가 변경되었습니다.";

        return "<!DOCTYPE html>" +
                "<html lang=\"ko\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Sequence 프로젝트 정보 변경</title>" +
                "<style>" +
                "@media only screen and (max-width: 600px) {" +
                ".container { width: 100% !important; max-width: 100% !important; }" +
                ".mobile-padding { padding: 20px !important; }" +
                ".mobile-text { font-size: 16px !important; }" +
                ".mobile-title { font-size: 18px !important; }" +
                ".mobile-hide { display: none !important; }" +
                ".mobile-center { text-align: center !important; }" +
                "}" +
                "@media only screen and (max-width: 480px) {" +
                ".mobile-padding { padding: 15px !important; }" +
                ".mobile-text { font-size: 14px !important; }" +
                ".mobile-title { font-size: 16px !important; }" +
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
                "<h2 class=\"mobile-title\" style=\"font-size: 24px; color: #ffffff; margin: 0 0 10px 0; font-weight: 700; font-family: Arial, sans-serif; line-height: 1.3;\">📝 프로젝트 정보 업데이트</h2>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 16px; margin: 0; font-family: Arial, sans-serif; line-height: 1.4;\">참여 중인 프로젝트의 정보가 변경되었습니다</p>" +
                "</td>" +
                "</tr>" +

                // 컨텐츠 섹션
                "<tr>" +
                "<td class=\"mobile-padding\" style=\"padding: 40px 30px; background-color: #151515;\">" +

                // 인사말
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 25px;\">" +
                "<tr>" +
                "<td style=\"padding: 0;\">" +
                "<p class=\"mobile-text\" style=\"font-size: 18px; color: #ffffff; margin: 0; font-family: Arial, sans-serif; line-height: 1.5;\">" +
                "안녕하세요 <span style=\"color: #ffffff; background-color: #E32929; padding: 3px 10px; border-radius: 4px; font-weight: bold; white-space: nowrap;\">" + nickname + "</span>님," +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                // 업데이트 메시지
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin-bottom: 30px;\">" +
                "<tr>" +
                "<td style=\"padding: 0;\">" +
                "<p class=\"mobile-text\" style=\"font-size: 16px; color: #cccccc; margin: 0; line-height: 1.6; font-family: Arial, sans-serif;\">" +
                "참여 중인 <strong style=\"color: #ffffff;\">" + projectName + "</strong> 프로젝트의 정보가 변경되었습니다." +
                "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                // 변경 내용 섹션
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 25px 0;\">" +
                "<tr>" +
                "<td style=\"background-color: #1a1a1a; border: 2px solid #E32929; border-radius: 8px; padding: 25px 20px;\">" +
                "<h3 class=\"mobile-title\" style=\"color: #E32929; font-size: 16px; font-weight: 700; margin: 0 0 15px 0; text-transform: uppercase; font-family: Arial, sans-serif;\">변경 내용</h3>" +
                "<div style=\"background-color: #0f0f0f; color: #ffffff; font-size: 14px; padding: 20px; border-radius: 4px; line-height: 1.6; border-left: 4px solid #E32929; font-family: Arial, sans-serif; word-break: break-word;\">" +
                details +
                "</div>" +
                "</td>" +
                "</tr>" +
                "</table>" +

                // 안내 섹션
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"margin: 25px 0;\">" +
                "<tr>" +
                "<td style=\"background-color: #1a1a1a; padding: 20px; border-radius: 6px;\">" +
                "<h3 class=\"mobile-title\" style=\"color: #E32929; font-size: 16px; font-weight: 700; margin: 0 0 15px 0; font-family: Arial, sans-serif;\">💡 안내사항</h3>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 8px 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 프로젝트 정보 변경 알림</p>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 8px 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 상세 내용은 로그인하여 프로젝트 페이지에서 확인 가능합니다</p>" +
                "<p class=\"mobile-text\" style=\"color: #cccccc; font-size: 14px; margin: 8px 0; line-height: 1.6; font-family: Arial, sans-serif;\">• 변경사항에 대한 문의는 프로젝트 관리자에게 연락해주세요</p>" +
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
    }
}
