package sequence.sequence_member.report.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sequence.sequence_member.global.enums.enums.Degree;
import sequence.sequence_member.report.entity.ReportEntity;

@Getter
@AllArgsConstructor
public class ReportTargetDTO {
    private String nickname;
    private String schoolName;
    private String major;
    private String grade;
    private Degree degree;
    private ReportEntity.ReportTarget reportTarget;
    private Long targetId;
}
