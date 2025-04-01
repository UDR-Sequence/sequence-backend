package sequence.sequence_member.report.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import sequence.sequence_member.report.entity.ReportEntity;

import java.util.List;

@Data
@Builder
@Getter
public class ReportResponseDTO {
    private Long id;
    private String nickname;
    private String reporter;
    private List<String> reportTypes;
    private ReportEntity.ReportTarget reportTarget;
    private Long targetId;
    private String reportContent;
}

