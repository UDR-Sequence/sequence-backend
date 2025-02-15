package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import lombok.*;

import org.hibernate.validator.constraints.Length;
import sequence.sequence_member.global.enums.enums.Degree;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.global.enums.enums.Skill;
import sequence.sequence_member.member.entity.MemberEntity.Gender;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberDTO {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min= 4, max= 10, message = "아이디는 최소 4자 이상 최대 10자 이하입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 20, message = "비밀번호는 8~20자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    private Date birth;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    @NotBlank(message = "주소지는 필수 입력 값입니다.")
    private String address;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String introduction;
    private List<String> portfolio; // todo - minio에 저장할 수 있도록 추가해야 함

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "학교명은 필수 입력 값입니다.")
    private String schoolName;

    @NotBlank(message = "전공은 필수 입력 값입니다.")
    private String major;

    @NotBlank(message = "학년은 필수 입력 값입니다.")
    @Pattern(regexp = "^[1-6]학년$", message = "학년은 1학년부터 6학년까지 입력 가능합니다.")
    private String grade;

    private Date entranceDate;

    private Date graduationDate;

    @NotNull(message = "학위는 필수 입력 값입니다.")
    private Degree degree;

    private List<Skill> skillCategory;
    private List<ProjectRole> desiredJob;
    private List<ExperienceDTO> experiences;
    private List<CareerInputDTO> careers;
    private List<AwardInputDTO> awards;

}
