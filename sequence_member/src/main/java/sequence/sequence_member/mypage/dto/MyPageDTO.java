package sequence.sequence_member.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sequence.sequence_member.global.enums.enums.AwardType;
import sequence.sequence_member.global.enums.enums.Degree;
import sequence.sequence_member.global.enums.enums.ExperienceType;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.global.enums.enums.Skill;
import sequence.sequence_member.member.entity.MemberEntity.Gender;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class MyPageDTO {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min= 4, max= 10, message = "아이디는 최소 4자 이상 최대 10자 이하입니다.")
    private String username;

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

    private List<AwardDTO> awards;
    private List<CareerDTO> careers;
    private List<ExperienceDTO> experiences;

    @Data
    public static class AwardDTO {
        private AwardType awardType;
        private String organizer;
        private String awardName;
        private Date awardDuration;
    }

    @Data
    public static class CareerDTO {
        private String companyName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String careerDescription;
    }

    @Data
    public static class ExperienceDTO {
        private ExperienceType experienceType;
        private String experienceName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String experienceDescription;
    }
}
