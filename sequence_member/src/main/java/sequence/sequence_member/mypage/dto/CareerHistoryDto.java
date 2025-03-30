package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.AwardType;
import sequence.sequence_member.global.enums.enums.ExperienceType;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 사용자의 경력 및 활동 이력 DTO
 *
 * 사용자가 가진 직무 경험, 학력, 프로젝트 경험 등을 저장하는 객체.
 * 마이페이지 화면에서 '경력 및 활동이력'에 해당하는 객체
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CareerHistoryDto {
    private String introduction;
    private List<ExperienceDTO> experiences;
    private List<CareerDTO> careers;
    private List<AwardDTO> awards;
    private List<PortfolioDTO> portfolios;

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
    public static class PortfolioDTO {
        private final String portfolioUrl;

        public PortfolioDTO(String portfolioUrl) {
            this.portfolioUrl = portfolioUrl;
        }
    }
}
