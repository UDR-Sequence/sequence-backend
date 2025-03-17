package sequence.sequence_member.mypage.dto;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyPageMapper {

    private final ArchiveService archiveService;

    public MyPageMapper(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    /**
     * 멤버와 아카이브 페이지네이션 객체를 ResponseDTO 매핑하는 함수입니다.
     *
     * @param member ResponseDTO로 매핑할 멤버 객체
     * @param archivePage ResponseDTO로 매핑할 archive 페이지네이션 객체
     * @return 사용자의 마이페이지 정보를 담은 MyPageResponseDTO
     */
    public MyPageResponseDTO toDTO(MemberEntity member, Page<Archive> archivePage) {
        MyPageResponseDTO dto = new MyPageResponseDTO(
                member.getUsername(),
                member.getName(),
                member.getBirth(),
                member.getGender(),
                member.getAddress(),
                member.getPhone(),
                member.getIntroduction(),
                member.getNickname(),
                member.getEducation().getSchoolName(),
                member.getEducation().getMajor(),
                member.getEducation().getGrade(),
                member.getEducation().getEntranceDate(),
                member.getEducation().getGraduationDate(),
                member.getEducation().getDegree(),
                member.getEducation().getSkillCategory(),
                member.getEducation().getDesiredJob()
        );

        // PortfolioDTO 리스트 생성
        List<MyPageResponseDTO.PortfolioDTO> portfolios = member.getPortfolios().stream()
                .map(portfolio -> new MyPageResponseDTO.PortfolioDTO(
                        portfolio.getPortfolioUrl()
                ))
                .collect(Collectors.toList());
        dto.setPortfolios(portfolios);

        // AwardDTO 리스트 생성
        List<MyPageResponseDTO.AwardDTO> awards = member.getAwards().stream()
                .map(award -> new MyPageResponseDTO.AwardDTO(
                        award.getAwardType(),
                        award.getOrganizer(),
                        award.getAwardName(),
                        award.getAwardDuration()
                ))
                .collect(Collectors.toList());
        dto.setAwards(awards);

        // CareerDTO 리스트 생성
        List<MyPageResponseDTO.CareerDTO> careers = member.getCareers().stream()
                .map(career -> new MyPageResponseDTO.CareerDTO(
                        career.getCompanyName(),
                        career.getStartDate(),
                        career.getEndDate(),
                        career.getCareerDescription()
                ))
                .collect(Collectors.toList());
        dto.setCareers(careers);

        // ExperienceDTO 리스트 생성
        List<MyPageResponseDTO.ExperienceDTO> experiences = member.getExperiences().stream()
                .map(experience -> new MyPageResponseDTO.ExperienceDTO(
                        experience.getExperienceType(),
                        experience.getExperienceName(),
                        experience.getStartDate(),
                        experience.getEndDate(),
                        experience.getExperienceDescription()
                ))
                .collect(Collectors.toList());
        dto.setExperiences(experiences);

        // ArchiveDTO 리스트 생성
        ArchivePageResponseDTO archivePageResponseDTO = archiveService.createArchivePageResponse(archivePage, member.getUsername());
        dto.setArchivePageResponseDTO(archivePageResponseDTO);

        return dto;
    }
}