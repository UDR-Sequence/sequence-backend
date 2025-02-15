package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;

@Getter
public class CareerInputDTO {

    @NotBlank(message = "회사명을 입력해주세요.")
    private String companyName;

    @NotBlank(message = "시작 기간을 입력해주세요.")
    private LocalDate startDate;

    @NotBlank(message = "종료 기간을 입력해주세요.")
    private LocalDate endDate;

    @NotBlank(message = "맡았던 직무와 업무를 입력해주세요.")
    private String careerDescription;
}
