package sequence.sequence_member.project.dto;

import lombok.Builder;
import lombok.Getter;
import sequence.sequence_member.global.enums.enums.Category;

@Getter
@Builder
public class ProjectSummaryInfoDTO {

    private Long projectId;
    private String projectTitle;
    private String projectName;
    private String projectWriterNickname;
    private Category category;
}
