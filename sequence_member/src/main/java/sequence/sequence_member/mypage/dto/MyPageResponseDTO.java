package sequence.sequence_member.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
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
public class MyPageResponseDTO {
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

    private List<PortfolioDTO> portfolios;
    private List<Skill> skillCategory;
    private List<ProjectRole> desiredJob;

    private List<AwardDTO> awards;
    private List<CareerDTO> careers;
    private List<ExperienceDTO> experiences;

    private ArchivePageResponseDTO archivePageResponseDTO;

    private MyActivityResponseDTO myActivityResponseDTO;

    public MyPageResponseDTO(
            String username, String name, Date birth, Gender gender, String address,
            String phone, String introduction, String nickname,
            String schoolName, String major, String grade, Date entranceDate, Date graduationDate,
            Degree degree, List<Skill> skillCategory, List<ProjectRole> desiredJob) {
        this.username = username;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.introduction = introduction;
        this.nickname = nickname;
        this.schoolName = schoolName;
        this.major = major;
        this.grade = grade;
        this.entranceDate = entranceDate;
        this.graduationDate = graduationDate;
        this.degree = degree;
        this.skillCategory = skillCategory;
        this.desiredJob = desiredJob;
    }

    @Data
    public static class AwardDTO {
        private final AwardType awardType;
        private final String organizer;
        private final String awardName;
        private final Date awardDuration;

        public AwardDTO(AwardType awardType, String organizer, String awardName, Date awardDuration) {
            this.awardType = awardType;
            this.organizer = organizer;
            this.awardName = awardName;
            this.awardDuration = awardDuration;
        }
    }

    @Data
    public static class CareerDTO {
        private final String companyName;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String careerDescription;

        public CareerDTO(String companyName, LocalDate startDate, LocalDate endDate, String careerDescription) {
            this.companyName = companyName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.careerDescription = careerDescription;
        }
    }

    @Data
    public static class ExperienceDTO {
        private final ExperienceType experienceType;
        private final String experienceName;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String experienceDescription;

        public ExperienceDTO(ExperienceType experienceType, String experienceName, LocalDate startDate,
                             LocalDate endDate, String experienceDescription) {
            this.experienceType = experienceType;
            this.experienceName = experienceName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.experienceDescription = experienceDescription;
        }
    }

    @Data
    public static class PortfolioDTO {
        private final String portfolioUrl;

        public PortfolioDTO(String portfolioUrl) {
            this.portfolioUrl = portfolioUrl;
        }
    }

    public void setMyActivityResponseDTO(MyActivityResponseDTO myActivityResponseDTO){
        this.myActivityResponseDTO = myActivityResponseDTO;
    }

}
