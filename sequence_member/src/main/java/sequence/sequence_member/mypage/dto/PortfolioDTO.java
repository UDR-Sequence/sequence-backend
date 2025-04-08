package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 사용자가 참여한 아카이브 이력 DTO
 *
 * 마이페이지 화면에서 '포트폴리오'에 해당하는 객체
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO {
    private Page<ArchiveSummaryDTO> archivePage;
    private List<InvitedProjectWithCommentDTO> invitedProjects;
}
