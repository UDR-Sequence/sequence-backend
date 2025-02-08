package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Getter;
import sequence.sequence_member.global.enums.enums.AwardType;

@Getter
public class AwardInputDTO {

    @NotNull(message = "구분을 선택해주세요")
    private AwardType awardType;

    @NotBlank(message = "수상기관을 입력해주세요.")
    private String organizer; //수상기관

    @NotNull(message = "수상일을 입력해주세요.")
    private Date awardDuration;

    @NotBlank(message = "수상명을 입력해주세요.")
    private String awardName;

}
