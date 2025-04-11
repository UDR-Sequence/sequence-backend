package sequence.sequence_member.report.dto;

import com.sun.jna.platform.win32.WinNT;
import lombok.Data;
import sequence.sequence_member.report.entity.ReportEntity;

import java.util.List;

@Data
public class ReportRequestDTO {
    private String nickname;
    private ReportEntity.ReportType reportType;
    private ReportEntity.ReportTarget reportTarget;
    private String reportContent;
    private Long targetId;
}

