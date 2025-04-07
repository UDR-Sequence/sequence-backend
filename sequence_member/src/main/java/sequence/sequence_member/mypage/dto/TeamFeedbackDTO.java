package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sequence.sequence_member.archive.dto.MyPageEvaluationDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamFeedbackDTO {
    private MyPageEvaluationDTO myPageEvaluation;
}
