package sequence.sequence_member.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectMemberOutputDTO {
    private Long memberId;
    private String profileImgUrl;
    private String nickname;
}
