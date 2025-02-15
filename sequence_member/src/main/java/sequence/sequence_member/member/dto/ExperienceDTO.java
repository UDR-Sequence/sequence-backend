package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import sequence.sequence_member.global.enums.enums.ExperienceType;

@Getter
public class ExperienceDTO {

    @NotNull(message = "구분을 선택해주세요.")
    private ExperienceType experienceType;

    @NotBlank(message = "경험명을 입력해주세요.")
    private String experienceName;

    @NotBlank(message = "시작 기간을 입력해주세요.")
    private LocalDate startDate;

    @NotBlank(message = "종료 기간을 입력해주세요.")
    private LocalDate endDate;

    @NotBlank(message = "내용을 입력해주세요.")
    private String experienceDescription;
}
