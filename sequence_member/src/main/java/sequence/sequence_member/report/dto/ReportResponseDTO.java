package sequence.sequence_member.report.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class ReportResponseDTO {
    private String reportType;
    private String reportContent;
    private String reporter;
}
