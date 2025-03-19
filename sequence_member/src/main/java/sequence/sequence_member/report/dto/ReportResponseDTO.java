package sequence.sequence_member.report.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import sequence.sequence_member.report.entity.ReportEntity;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@Getter
public class ReportResponseDTO {
    private Long id;
    private String nickname;
    private String reporter;
    private List<String> reportTypes;  // 한글 설명 리스트
    private String reportContent;
}
