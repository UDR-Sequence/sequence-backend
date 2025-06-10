package sequence.sequence_member.project.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.BaseException;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.project.entity.Project;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectInviteEmailService {
    private final JavaMailSender mailSender;

    @Value("${NAVER_MAIL_USERNAME:dev_mj_@naver.com}")
    private String fromEmail;

//    프로젝트 초대 이메일 발송
    public void sendInviteEmail(Project project, MemberEntity invitedMember) {
        try{
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
                    invitedMember.getEmail(), project.getTitle());

        } catch (MessagingException e) {
            throw new BaseException(Code.INTERNAL_SERVER_ERROR, "프로젝트 초대 이메일 발송에 실패했습니다.");
        }
    }

    // 초대 이메일 HTML
    private String createInvitationHtmlContent(Project project, MemberEntity invitedMember) {
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
                    
                    <div style="text-align: center; margin: 40px 0;">
                        <a href="https://sequence-zeta.vercel.app/" 
                           style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                  color: white; text-decoration: none; padding: 15px 30px;
                                  border-radius: 25px; font-size: 16px; font-weight: bold;
                                  display: inline-block; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);">
                            ✨ 프로젝트 확인하기
                        </a>
                    </div>
                    
                    <div style="background: #e3f2fd; padding: 20px; border-radius: 8px; margin-top: 30px;">
                        <p style="margin: 0; font-size: 14px; color: #1976d2;">
                            💡 <strong>참고사항:</strong><br>
                            이 초대를 수락하시려면 위 버튼을 클릭하여 프로젝트 페이지에서 참가 승인을 해주세요.
                        </p>
                    </div>
                </div>
                
                <div style="background: #f5f5f5; padding: 20px; text-align: center; color: #666; font-size: 12px;">
                    <p style="margin: 0;">이 이메일은 자동으로 발송된 메일입니다.</p>
                    <p style="margin: 5px 0 0 0;">프로젝트 협업 플랫폼 © 2025</p>
                </div>
            </div>
            """,
            invitedMember.getNickname(),
            project.getWriter().getNickname(),
            project.getTitle(),
            project.getProjectName(),
            project.getCategory().toString(),
            project.getPersonnel(),
            project.getStartDate(),
            project.getEndDate(),
            project.getId()
        );
    }
}
