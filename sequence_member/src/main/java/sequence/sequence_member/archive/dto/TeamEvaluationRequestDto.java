package sequence.sequence_member.archive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEvaluationRequestDto {

    @NotNull(message = "평가 받는 팀원의 ID는 필수입니다.")
    private Long evaluatedMemberId;    // 평가 받는 팀원의 ID
    
    @NotNull(message = "피드백 내용은 필수입니다.")
    private String feedback;           // 피드백 내용
    
    @NotNull(message = "키워드는 필수입니다.")
    private String keyword;            // 키워드 (JSON 형태)
    
    @NotNull(message = "한줄 피드백은 필수입니다.")
    private String lineFeedback;       // 한줄 피드백
} 