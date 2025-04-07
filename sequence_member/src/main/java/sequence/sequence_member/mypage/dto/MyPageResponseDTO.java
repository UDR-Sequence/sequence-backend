package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 API 응답 DTO
 *
 * 마이페이지에서 반환되는 데이터를 하나의 객체로 묶어 관리한다.
 * 기본 정보, 경력 및 활동 이력, 포트폴리오, 팀원들의 평가, 내 활동 정보를 포함한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDTO {
    private BasicInfoDTO basicInfo;         // 기본적인 정보
    private CareerHistoryDTO careerHistory; // 경력 및 활동 이력
    private PortfolioDTO portfolio;         // 포트폴리오
    private TeamFeedbackDTO teamFeedback;   // 팀원들의 평가
    private MyActivitiesDTO myActivities;   // 내 활동
}
