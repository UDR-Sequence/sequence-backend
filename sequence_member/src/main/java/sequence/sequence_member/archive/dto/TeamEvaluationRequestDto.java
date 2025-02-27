package sequence.sequence_member.archive.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import jakarta.validation.Valid;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEvaluationRequestDto {
    @NotNull(message = "평가 목록은 필수입니다.")
    @Valid
    private List<EvaluationItem> evaluations;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluationItem {
        @NotNull(message = "평가 받는 팀원의 username은 필수입니다.")
        private String evaluatedUserName;
        
        @NotNull(message = "피드백 내용은 필수입니다.")
        private String feedback;
        
        @NotNull(message = "키워드는 필수입니다.")
        private List<String> keyword;
    }
} 