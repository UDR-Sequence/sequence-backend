package sequence.sequence_member.mypage.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.dto.MyPageEvaluationDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveBookmarkRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.archive.service.MyPageEvaluationService;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteProjectOutputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.service.InviteAccessService;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyPageMapper {
    private final ArchiveService archiveService;
    private final ArchiveRepository archiveRepository;
    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final ArchiveBookmarkRepository archiveBookmarkRepository;
    private final InviteAccessService inviteAccessService;
    private final MyPageEvaluationService myPageEvaluationService;

    /**
     * 멤버와 아카이브 페이지네이션 객체를 ResponseDTO 매핑하는 메인 함수입니다.
     *
     * @param member ResponseDTO로 매핑할 멤버 객체
     * @param archivePage ResponseDTO로 매핑할 archive 페이지네이션 객체
     * @return 사용자의 마이페이지 정보를 담은 MyPageResponseDTO
     */
    public MyPageResponseDTO toMyPageResponseDto(MemberEntity member, Page<Archive> archivePage, CustomUserDetails customUserDetails) {
        return new MyPageResponseDTO(
                toBasicInfoDto(member),
                toCareerHistoryDto(member),
                toPortfolioDto(member, archivePage, customUserDetails),
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
                .entranceDate(member.getEducation().getEntranceDate())
                .graduationDate(member.getEducation().getGraduationDate())
                .degree(member.getEducation().getDegree())
                .skillCategory(member.getEducation().getSkillCategory())
                .desiredJob(member.getEducation().getDesiredJob())
                .build();
    }

    /**
     * 경력 및 활동 이력을 MyPageResponseDTO 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return CareerHistoryDto 객체
     */
    private CareerHistoryDTO toCareerHistoryDto(MemberEntity member) {
        return CareerHistoryDTO.builder()
                .introduction(member.getIntroduction())
                .experiences(member.getExperiences().stream()
                        .map(e -> CareerHistoryDTO.ExperienceDTO.builder()
                                .experienceType(e.getExperienceType())
                                .experienceName(e.getExperienceName())
                                .startDate(e.getStartDate())
                                .endDate(e.getEndDate())
                                .experienceDescription(e.getExperienceDescription())
                                .build())
                        .collect(Collectors.toList()))
                .careers(member.getCareers().stream()
                        .map(c -> CareerHistoryDTO.CareerDTO.builder()
                                .companyName(c.getCompanyName())
                                .startDate(c.getStartDate())
                                .endDate(c.getEndDate())
                                .careerDescription(c.getCareerDescription())
                                .build())
                        .collect(Collectors.toList()))
                .awards(member.getAwards().stream()
                        .map(a -> CareerHistoryDTO.AwardDTO.builder()
                                .awardType(a.getAwardType())
                                .organizer(a.getOrganizer())
                                .awardName(a.getAwardName())
                                .awardDuration(a.getAwardDuration())
                                .build())
                        .collect(Collectors.toList()))
                .portfolios(member.getPortfolios().stream()
                        .map(p -> CareerHistoryDTO.PortfolioDTO.builder()
                                .portfolioUrl(p.getPortfolioUrl())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 아카이브 리스트와 초대된 프로젝트와 PortfolioDto 형식으로 변환합니다.
     *
     * @param member      ResponseDTO로 변환할 멤버 객체
     * @param archivePage
     * @param customUserDetails inviteAccessService에서 사용하기 위한 객체
     * @return PortfolioDto 객체
     */
    private PortfolioDTO toPortfolioDto(MemberEntity member, Page<Archive> archivePage, CustomUserDetails customUserDetails) {
        ArchivePageResponseDTO archiveDTO = archiveService.createArchivePageResponse(archivePage, member.getUsername());
         List<InviteProjectOutputDTO> invitedProjects = inviteAccessService.getInvitedProjects(customUserDetails);
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

        List<MyActivitiesDTO.PostDTO> archiveWritten = archiveRepository.findByWriter(member, sort).stream()
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
                .createdDate(archive.getCreatedDateTime())
                .numberOfComments(archive.getComments().size())
                .build();
    }

    // Project 객체를 PostDTO로 변환
    private MyActivitiesDTO.PostDTO mapToPostDTO(Project project) {
        return MyActivitiesDTO.PostDTO.builder()
                .title(project.getTitle())
                .articleId(project.getId())
                .createdDate(project.getCreatedDateTime())
                .numberOfComments(project.getComments().size())
                .build();
    }
}
