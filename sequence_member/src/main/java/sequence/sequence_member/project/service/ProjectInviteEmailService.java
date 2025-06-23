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
import sequence.sequence_member.global.exception.BaseException;
import sequence.sequence_member.global.response.Code;
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

    // 초대 이메일 HTML 템플릿
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

        return String.format("""
            <div style="max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                           padding: 30px; text-align: center; color: white;">
                    <h1 style="margin: 0; font-size: 28px;">🎉 프로젝트 초대장</h1>
                    <p style="margin: 10px 0 0 0; font-size: 16px; opacity: 0.9;">
                        새로운 프로젝트에 초대되었습니다!
                    </p>
                </div>
                
                <div style="background: white; padding: 40px; border-left: 4px solid #667eea;">
                    <p style="font-size: 18px; color: #333; margin-bottom: 20px;">
                        안녕하세요 <strong>%s</strong>님! 👋
                    </p>
                    
                    <p style="font-size: 16px; color: #666; line-height: 1.6; margin-bottom: 30px;">
                        <strong>%s</strong>님이 회원님을 다음 프로젝트에 초대했습니다.
                    </p>
                    
                    <div style="background: #f8f9fa; padding: 25px; border-radius: 8px; margin: 30px 0;">
                        <h2 style="color: #333; margin: 0 0 15px 0; font-size: 22px;">
                            📋 %s
                        </h2>
                        <p style="color: #666; margin: 5px 0; font-size: 14px;">
                            <strong>프로젝트명:</strong> %s
                        </p>
                        <p style="color: #666; margin: 5px 0; font-size: 14px;">
                            <strong>카테고리:</strong> %s
                        </p>
                        <p style="color: #666; margin: 5px 0; font-size: 14px;">
                            <strong>모집인원:</strong> %d명
                        </p>
                        <p style="color: #666; margin: 5px 0; font-size: 14px;">
                            <strong>프로젝트 기간:</strong> %s ~ %s
                        </p>
                    </div>
                    
                    <div style="background: #e3f2fd; padding: 20px; border-radius: 8px; margin-top: 30px;">
                        <p style="margin: 0; font-size: 14px; color: #1976d2; text-align: center;">
                            💡 <strong>안내:</strong><br>
                            프로젝트 초대 알림입니다. 참여를 원하시면 로그인하여 확인해주세요.
                        </p>
                    </div>
                </div>
                
                <div style="background: #f5f5f5; padding: 20px; text-align: center; color: #666; font-size: 12px;">
                    <p style="margin: 0;">이 이메일은 자동으로 발송된 메일입니다.</p>
                    <p style="margin: 5px 0 0 0;">Sequence © 2025</p>
                </div>
            </div>
            """,
                nickname, writerNickname, title, projectName, category, personnel,
                startDate, endDate
        );
    }

    // 프로젝트 수정 알림 이메일 HTML 템플릿
    private String createUpdateHtmlContent(Project project, MemberEntity member, String updateDetails) {
        String nickname = member.getNickname() != null ? member.getNickname() : "회원";
        String projectName = project.getProjectName() != null ? project.getProjectName() : "프로젝트";
        String details = updateDetails != null && !updateDetails.isEmpty() ? updateDetails : "프로젝트 정보가 변경되었습니다.";

        return String.format("""
            <div style="max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                           padding: 30px; text-align: center; color: white;">
                    <h1 style="margin: 0; font-size: 28px;">📝 프로젝트 정보 변경</h1>
                    <p style="margin: 10px 0 0 0; font-size: 16px; opacity: 0.9;">
                        프로젝트 정보가 업데이트되었습니다!
                    </p>
                </div>
                
                <div style="background: white; padding: 40px; border-left: 4px solid #667eea;">
                    <p style="font-size: 18px; color: #333; margin-bottom: 20px;">
                        안녕하세요 <strong>%s</strong>님! 👋
                    </p>
                    
                    <p style="font-size: 16px; color: #666; line-height: 1.6; margin-bottom: 30px;">
                        참여 중인 <strong>%s</strong> 프로젝트의 정보가 변경되었습니다.
                    </p>
                    
                    <div style="background: #fff3cd; padding: 20px; border-radius: 8px; margin: 30px 0; border-left: 4px solid #ffc107;">
                        <h3 style="color: #856404; margin: 0 0 10px 0;">변경 내용</h3>
                        <p style="color: #856404; margin: 0; font-size: 14px;">%s</p>
                    </div>
                    
                    <div style="background: #e3f2fd; padding: 20px; border-radius: 8px; margin-top: 30px;">
                        <p style="margin: 0; font-size: 14px; color: #1976d2; text-align: center;">
                            💡 <strong>안내:</strong><br>
                            프로젝트 변경 알림입니다. 자세한 내용은 로그인하여 확인해주세요.
                        </p>
                    </div>
                </div>
                
                <div style="background: #f5f5f5; padding: 20px; text-align: center; color: #666; font-size: 12px;">
                    <p style="margin: 0;">이 이메일은 자동으로 발송된 메일입니다.</p>
                    <p style="margin: 5px 0 0 0;">Sequence © 2025</p>
                </div>
            </div>
            """,
                nickname, projectName, details
        );
    }
}
