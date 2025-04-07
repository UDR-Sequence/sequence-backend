package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 마이페이지 API 응답 DTO
 *
 * 마이페이지에서 반환되는 데이터를 하나의 객체로 묶어 관리한다.
 * 기본 정보, 경력 및 활동 이력, 포트폴리오, 팀원들의 평가, 내 활동 정보를 포함한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDto {
    private BasicInfoDto basicInfo;         // 기본적인 정보
    private CareerHistoryDto careerHistory; // 경력 및 활동 이력
    private PortfolioDto portfolio;         // 포트폴리오
    private TeamFeedbackDto teamFeedback;   // 팀원들의 평가
    private MyActivitiesDto myActivities;   // 내 활동
}
