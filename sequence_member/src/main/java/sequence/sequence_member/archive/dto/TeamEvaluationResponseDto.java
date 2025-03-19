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
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.member.entity.EducationEntity;

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
        private String nickname;
        private String profileImg;
        private List<ProjectRole> roles;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluatedInfo {
        private String nickname;
        private String profileImg;
        private List<ProjectRole> roles;
    }

    public static TeamEvaluationResponseDto from(TeamEvaluation evaluation) {
        EducationEntity evaluatorEducation = evaluation.getEvaluator().getMember().getEducation();
        List<ProjectRole> evaluatorRoles = evaluatorEducation != null ? 
                                          evaluatorEducation.getDesiredJob() : 
                                          new ArrayList<>();

        EducationEntity evaluatedEducation = evaluation.getEvaluated().getMember().getEducation();
        List<ProjectRole> evaluatedRoles = evaluatedEducation != null ? 
                                          evaluatedEducation.getDesiredJob() : 
                                          new ArrayList<>();

        return TeamEvaluationResponseDto.builder()
                .evaluator(EvaluatorInfo.builder()
                    .nickname(evaluation.getEvaluator().getMember().getNickname())
                    .profileImg(evaluation.getEvaluator().getMember().getProfileImg())
                    .roles(evaluatorRoles)
                    .build())
                .evaluated((List<EvaluatedInfo>) EvaluatedInfo.builder()
                    .nickname(evaluation.getEvaluated().getMember().getNickname())
                    .profileImg(evaluation.getEvaluated().getMember().getProfileImg())
                    .roles(evaluatedRoles)
                    .build())
                .build();
    }
} 