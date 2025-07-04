package sequence.sequence_member.mypage.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import sequence.sequence_member.archive.dto.MyPageEvaluationDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveBookmarkRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.archive.service.MyPageEvaluationService;
import sequence.sequence_member.member.converter.SkillCategoryConverter;
import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.entity.PortfolioEntity;
import sequence.sequence_member.member.repository.AwardRepository;
import sequence.sequence_member.member.repository.CareerRepository;
import sequence.sequence_member.member.repository.ExperienceRepository;
import sequence.sequence_member.member.repository.PortfolioRepository;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyPageMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ArchiveRepository archiveRepository;
    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final ArchiveBookmarkRepository archiveBookmarkRepository;
    private final MyPageEvaluationService myPageEvaluationService;
    private final ExperienceRepository experienceRepository;
    private final CareerRepository careerRepository;
    private final AwardRepository awardRepository;
    private final PortfolioRepository portfolioRepository;
    private final SkillCategoryConverter skillCategoryConverter;

    /**
     * 멤버와 아카이브 페이지네이션 객체를 ResponseDTO 매핑하는 메인 함수입니다.
     *
     * @param member ResponseDTO로 매핑할 멤버 객체
     * @param archiveList ResponseDTO로 매핑할 archive 페이지네이션 객체
     * @return 사용자의 마이페이지 정보를 담은 MyPageResponseDTO
     */
    public MyPageResponseDTO toMyPageResponseDto(
            MemberEntity member,
            List<Archive> archiveList,
            List<InvitedProjectWithCommentDTO> invitedProjects
    ) {
        return new MyPageResponseDTO(
                toBasicInfoDto(member),
                toCareerHistoryDto(member),
                toPortfolioDto(archiveList, invitedProjects),
                toTeamFeedbackDto(member),
                getMyActivity(member)
        );
    }

    /**
     * 기본 정보를 MyPageResponseDTO 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return BasicInfoDto 객체
     */
    private BasicInfoDTO toBasicInfoDto(MemberEntity member) {
        return BasicInfoDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .birth(member.getBirth())
                .gender(member.getGender())
                .address(member.getAddress())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .schoolName(member.getEducation().getSchoolName())
                .major(member.getEducation().getMajor())
                .grade(member.getEducation().getGrade())
                .entranceYear(member.getEducation().getEntranceYear())
                .graduationYear(member.getEducation().getGraduationYear())
                .degree(member.getEducation().getDegree())
                .skillCategory(skillCategoryConverter.convertToSkillString(member.getEducation().getSkillCategory()))
                .desiredJob(member.getEducation().getDesiredJob())
                .profileImg(member.getProfileImg())
                .build();
    }

    /**
     * 경력 및 활동 이력을 MyPageResponseDTO 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return CareerHistoryDto 객체
     */
    private CareerHistoryDTO toCareerHistoryDto(MemberEntity member) {
        List<ExperienceEntity> experienceEntities = experienceRepository.findByMemberAndIsDeletedFalse(member);
        List<CareerEntity> careerEntities = careerRepository.findByMemberAndIsDeletedFalse(member);
        List<AwardEntity> awardEntities = awardRepository.findByMemberAndIsDeletedFalse(member);
        List<PortfolioEntity> portfolioEntities = portfolioRepository.findByMemberAndIsDeletedFalse(member);
        return CareerHistoryDTO.builder()
                .introduction(member.getIntroduction())
                .experiences(experienceEntities.stream()
                        .map(e -> CareerHistoryDTO.ExperienceDTO.builder()
                                .experienceType(e.getExperienceType())
                                .experienceName(e.getExperienceName())
                                .startDate(e.getStartDate())
                                .endDate(e.getEndDate())
                                .experienceDescription(e.getExperienceDescription())
                                .build())
                        .collect(Collectors.toList()))
                .careers(careerEntities.stream()
                        .map(c -> CareerHistoryDTO.CareerDTO.builder()
                                .companyName(c.getCompanyName())
                                .startDate(c.getStartDate())
                                .endDate(c.getEndDate())
                                .careerDescription(c.getCareerDescription())
                                .build())
                        .collect(Collectors.toList()))
                .awards(awardEntities.stream()
                        .map(a -> CareerHistoryDTO.AwardDTO.builder()
                                .awardType(a.getAwardType())
                                .organizer(a.getOrganizer())
                                .awardName(a.getAwardName())
                                .awardDuration(a.getAwardDuration())
                                .build())
                        .collect(Collectors.toList()))
                .portfolios(portfolioEntities.stream()
                        .map(p -> CareerHistoryDTO.PortfolioDTO.builder()
                                .portfolioUrl(p.getPortfolioUrl())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 내가 작성한 아카이브와 초대받은 프로젝트 리스트를 PortfolioDTO로 변환합니다.
     *
     * @param archiveList
     * @return PortfolioDto 객체
     */
    private PortfolioDTO toPortfolioDto(List<Archive> archiveList, List<InvitedProjectWithCommentDTO> invitedProjects) {
        List<ArchiveSummaryDTO> archiveDTO = archiveList.stream()
                .map(archive -> ArchiveSummaryDTO.builder()
                    .id(archive.getId())
                    .title(archive.getTitle())
                    .thumbnail(archive.getThumbnail())
                    .startDate(archive.getStartDate())
                    .endDate(archive.getEndDate())
                    .build())
                .collect(Collectors.toList());

        return new PortfolioDTO(archiveDTO, invitedProjects);
    }

    /**
     * 멤버의 활동 정보를 TeamFeedbackDto 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return TeamFeedbackDto 객체
     */
    public TeamFeedbackDTO toTeamFeedbackDto(MemberEntity member) {
        MyPageEvaluationDTO myPageEvaluationDTO = myPageEvaluationService.getMyEvaluation(member.getNickname());
        return new TeamFeedbackDTO(myPageEvaluationDTO);
    }

    /**
     * 멤버의 활동 정보를 MyActivitiesDto 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return MyActivitiesDto 객체
     */
    public MyActivitiesDTO getMyActivity(MemberEntity member) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDateTime");

        List<MyActivitiesDTO.PostDTO> archiveWritten = archiveRepository.findByWriterAndIsDeletedFalse(member, sort).stream()
                .map(this::mapToPostDTO)
                .collect(Collectors.toList());

        List<MyActivitiesDTO.PostDTO> archiveBookmarked = archiveBookmarkRepository.findAllByUserId(member, sort).stream()
                .map(bookmark -> mapToPostDTO(bookmark.getArchive()))
                .collect(Collectors.toList());

        MyActivitiesDTO.MyArchiveDTO myArchive = new MyActivitiesDTO.MyArchiveDTO(archiveWritten, archiveBookmarked);

        List<MyActivitiesDTO.PostDTO> projectWritten = projectRepository.findByWriter(member, sort).stream()
                .map(this::mapToPostDTO)
                .collect(Collectors.toList());

        List<MyActivitiesDTO.PostDTO> projectBookmarked = projectBookmarkRepository.findAllByMember(member, sort).stream()
                .map(bookmark -> mapToPostDTO(bookmark.getProject()))
                .collect(Collectors.toList());

        MyActivitiesDTO.MyProjectDTO myProject = new MyActivitiesDTO.MyProjectDTO(projectWritten, projectBookmarked);

        return new MyActivitiesDTO(myProject, myArchive);
    }

    // Archive 객체를 PostDTO로 변환
    private MyActivitiesDTO.PostDTO mapToPostDTO(Archive archive) {
        return MyActivitiesDTO.PostDTO.builder()
                .title(archive.getTitle())
                .articleId(archive.getId())
                .createdDate(archive.getCreatedDateTime().format(FORMATTER))
                .numberOfComments(archive.getComments().size())
                .build();
    }

    // Project 객체를 PostDTO로 변환
    private MyActivitiesDTO.PostDTO mapToPostDTO(Project project) {
        return MyActivitiesDTO.PostDTO.builder()
                .title(project.getTitle())
                .articleId(project.getId())
                .createdDate(project.getCreatedDateTime().format(FORMATTER))
                .numberOfComments(project.getComments().size())
                .build();
    }
}
