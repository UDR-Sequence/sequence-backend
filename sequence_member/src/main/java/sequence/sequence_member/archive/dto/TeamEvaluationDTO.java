package sequence.sequence_member.archive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import sequence.sequence_member.member.dto.MemberDTO;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamEvaluationDTO {
    private Long evaluationId;

    @NotBlank(message = "평가자 ID는 필수 입력 값입니다.")
    @Size(min= 3, max= 10, message = "아이디는 최소 3자 이상 최대 10자 이하입니다.")
    private MemberDTO evaluatorId;

    @NotBlank(message = "피평가자 ID는 필수 입력 값입니다.")
    @Size(min= 3, max= 10, message = "아이디는 최소 3자 이상 최대 10자 이하입니다.")
    private MemberDTO evaluateeId;

    @NotBlank(message = "피드백은 필수 입력 값입니다.")
    private String feedback;

    @NotBlank(message = "키워드는 필수 입력 값입니다.")
    private String keyword;

    private String lineFeedback;

    @NotBlank(message = "평가일자는 필수 입력 값입니다.")
    private LocalDate evaluationDate;
}

