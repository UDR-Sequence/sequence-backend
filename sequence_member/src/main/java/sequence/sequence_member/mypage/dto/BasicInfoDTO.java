package sequence.sequence_member.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Degree;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.global.enums.enums.Skill;
import sequence.sequence_member.member.entity.MemberEntity;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * 사용자의 기본 정보 DTO
 *
 * 마이페이지 화면에서 상단에 보여주는 기본 정보들에 해당하는 객체
 * 마이페이지에서 사용자 프로필에 표시되는 기본 정보를 담고 있다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfoDTO {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min= 4, max= 10, message = "아이디는 최소 4자 이상 최대 10자 이하입니다.")
    private String username;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    private LocalDate birth;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private MemberEntity.Gender gender;

    @NotBlank(message = "주소지는 필수 입력 값입니다.")
    private String address;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phone;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "학교명은 필수 입력 값입니다.")
    private String schoolName;

    @NotBlank(message = "전공은 필수 입력 값입니다.")
    private String major;

    @NotBlank(message = "학년은 필수 입력 값입니다.")
    @Pattern(regexp = "^[1-6]학년$", message = "학년은 1학년부터 6학년까지 입력 가능합니다.")
    private String grade;

    private Year entranceYear;

    private Year graduationYear;

    @NotNull(message = "학위는 필수 입력 값입니다.")
    private Degree degree;

    private List<String> skillCategory;
    private List<ProjectRole> desiredJob;

    private String profileImg;
}
