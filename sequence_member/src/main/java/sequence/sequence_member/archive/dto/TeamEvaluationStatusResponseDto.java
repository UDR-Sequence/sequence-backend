package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.global.enums.enums.Status;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEvaluationStatusResponseDto {
    private Map<String, MemberEvaluationStatus> memberStatus;
    private boolean isAllCompleted;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberEvaluationStatus {
        private String nickname;
        private List<ProjectRole> roles;
        private Status status;
    }
} 