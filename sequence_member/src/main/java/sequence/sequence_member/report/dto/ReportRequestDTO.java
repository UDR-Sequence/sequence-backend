package sequence.sequence_member.report.dto;

import lombok.Data;
import sequence.sequence_member.report.entity.ReportEntity;

import java.util.List;

@Data
public class ReportRequestDTO {
    private String nickname;
    private String reporter;
    private List<ReportEntity.ReportType> reportType;
    private ReportEntity.ReportTarget reportTarget;
    private String reportContent;
    private Long targetId;
}

