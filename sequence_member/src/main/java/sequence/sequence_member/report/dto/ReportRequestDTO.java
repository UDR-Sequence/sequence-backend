package sequence.sequence_member.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import sequence.sequence_member.report.entity.ReportEntity;

import java.util.List;

@Data
public class ReportRequestDTO {
    private String nickname;
    private String reporter;
    private List<ReportEntity.ReportType> reportType;
    private ReportEntity.ReportTarget reportTarget;
    private String reportContent;
    private Long postId;
}

