package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import sequence.sequence_member.member.entity.MemberEntity;

import java.time.LocalDate;

@Getter
@Setter
public class FindUsernameInputDTO {
    @NotBlank(message = "이름은 필수 입력 값 입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수 입력 값 입니다.")
    private LocalDate birth;

    @NotNull(message = "성별은 필수 입력 값 입니다.")
    private MemberEntity.Gender gender;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

}
