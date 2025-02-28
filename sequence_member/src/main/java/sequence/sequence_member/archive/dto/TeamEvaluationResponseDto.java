package sequence.sequence_member.archive.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import sequence.sequence_member.archive.entity.TeamEvaluation;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEvaluationResponseDto {
    private EvaluatorInfo evaluator;
    private List<EvaluatedInfo> evaluated;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluatorInfo {
        private String username;
        private String role;
        private String profileImg;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluatedInfo {
        private String username;
        private String role;
        private String profileImg;
    }

    public static TeamEvaluationResponseDto from(TeamEvaluation evaluation) {
        return TeamEvaluationResponseDto.builder()
                .evaluator(EvaluatorInfo.builder()
                    .username(evaluation.getEvaluator().getMember().getUsername())
                    .role(evaluation.getEvaluator().getRole())
                    .profileImg(evaluation.getEvaluator().getMember().getProfileImg())
                    .build())
                .evaluated((List<EvaluatedInfo>) EvaluatedInfo.builder()
                    .username(evaluation.getEvaluated().getMember().getUsername())
                    .role(evaluation.getEvaluated().getRole())
                    .profileImg(evaluation.getEvaluated().getMember().getProfileImg())
                    .build())
                .build();
    }
} 