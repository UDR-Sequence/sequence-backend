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
    private List<ReportEntity.ReportType> reportType;
    private String reporter;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min= 4, max= 10, message = "아이디는 최소 4자 이상 최대 10자 이하입니다.")
    private String reportContent;

}
