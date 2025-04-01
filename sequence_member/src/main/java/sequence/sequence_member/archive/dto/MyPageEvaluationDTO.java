package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageEvaluationDTO {
    private List<KeywordStatDTO> keywords;
    private List<FeedbackDetailDTO> feedbacks;
}