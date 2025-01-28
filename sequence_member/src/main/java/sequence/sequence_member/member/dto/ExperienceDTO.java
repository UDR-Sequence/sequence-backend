package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Getter;
import sequence.sequence_member.global.enums.enums.ExperienceType;

@Getter
public class ExperienceDTO {

    @NotNull(message = "구분을 선택해주세요.")
    private ExperienceType experienceType;

    @NotBlank(message = "경험명을 입력해주세요.")
    private String experienceName;

    @NotNull(message = "경험기간을 입력해주세요.")
    private Date experienceDuration;

    @NotBlank(message = "내용을 입력해주세요.")
    private String experienceDescription;
}
