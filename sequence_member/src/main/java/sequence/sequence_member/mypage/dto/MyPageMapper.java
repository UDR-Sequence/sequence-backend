package sequence.sequence_member.mypage.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.dto.MyPageEvaluationDTO;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveBookmark;
import sequence.sequence_member.archive.repository.ArchiveBookmarkRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.archive.service.MyPageEvaluationService;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteProjectOutputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.service.InviteAccessService;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectBookmark;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MyPageMapper {

    private final ArchiveService archiveService;
    private final ArchiveRepository archiveRepository;
    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final ArchiveBookmarkRepository archiveBookmarkRepository;
    private final InviteAccessService inviteAccessService;
    private final MyPageEvaluationService myPageEvaluationService;

    public MyPageMapper(
            ArchiveService archiveService,
            ArchiveRepository archiveRepository,
            ProjectRepository projectRepository,
            ProjectBookmarkRepository projectBookmarkRepository,
            ArchiveBookmarkRepository archiveBookmarkRepository,
            InviteAccessService inviteAccessService,
            MyPageEvaluationService myPageEvaluationService
    ) {
        this.archiveService = archiveService;
        this.archiveRepository = archiveRepository;
        this.projectRepository = projectRepository;
        this.projectBookmarkRepository = projectBookmarkRepository;
        this.archiveBookmarkRepository = archiveBookmarkRepository;
        this.inviteAccessService = inviteAccessService;
        this.myPageEvaluationService = myPageEvaluationService;
    }

    /**
     * 멤버와 아카이브 페이지네이션 객체를 ResponseDTO 매핑하는 메인 함수입니다.
     *
     * @param member ResponseDTO로 매핑할 멤버 객체
     * @param archivePage ResponseDTO로 매핑할 archive 페이지네이션 객체
     * @return 사용자의 마이페이지 정보를 담은 MyPageResponseDTO
     */
    public MyPageResponseDto toMyPageResponseDto(MemberEntity member, Page<Archive> archivePage, CustomUserDetails customUserDetails) {

        MyPageResponseDto dto = new MyPageResponseDto(
                toBasicInfoDto(member),
                toCareerHistoryDto(member),
                toPortfolioDto(member, archivePage, customUserDetails),
                toTeamFeedbackDto(member),
                getMyActivity(member)
        );

        return dto;
    }

    /**
     * 기본 정보를 MyPageResponseDTO 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return BasicInfoDto 객체
     */
    private BasicInfoDto toBasicInfoDto(MemberEntity member) {
        return new BasicInfoDto(
                member.getUsername(),
                member.getName(),
                member.getBirth(),
                member.getGender(),
                member.getAddress(),
                member.getPhone(),
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
    }

    /**
     * 경력 및 활동 이력을 MyPageResponseDTO 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return CareerHistoryDto 객체
     */
    private CareerHistoryDto toCareerHistoryDto(MemberEntity member) {
        List<CareerHistoryDto.ExperienceDTO> experiences = member.getExperiences().stream()
                .map(experience -> new CareerHistoryDto.ExperienceDTO(
                        experience.getExperienceType(),
                        experience.getExperienceName(),
                        experience.getStartDate(),
                        experience.getEndDate(),
                        experience.getExperienceDescription()
                ))
                .collect(Collectors.toList());

        List<CareerHistoryDto.CareerDTO> careers = member.getCareers().stream()
                .map(career -> new CareerHistoryDto.CareerDTO(
                        career.getCompanyName(),
                        career.getStartDate(),
                        career.getEndDate(),
                        career.getCareerDescription()
                ))
                .collect(Collectors.toList());

        List<CareerHistoryDto.AwardDTO> awards = member.getAwards().stream()
                .map(award -> new CareerHistoryDto.AwardDTO(
                        award.getAwardType(),
                        award.getOrganizer(),
                        award.getAwardName(),
                        award.getAwardDuration()
                ))
                .collect(Collectors.toList());

        // CareerHistoryDto 반환, 포트폴리오는 별도로 설정
        return new CareerHistoryDto(
                member.getIntroduction(),
                experiences,
                careers,
                awards,
                mapToPortfolioDTOs(member) // 사용자가 입력한 포트폴리오 파일 리스트 추가
        );
    }

    /**
     * 사용자가 입력한 포트폴리오 파일 리스트를 CareerHistoryDto 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return PortfolioDTO 리스트
     */
    private List<CareerHistoryDto.PortfolioDTO> mapToPortfolioDTOs(MemberEntity member) {
        return member.getPortfolios().stream()
                .map(portfolio -> new CareerHistoryDto.PortfolioDTO(
                        portfolio.getPortfolioUrl()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 아카이브 리스트와 초대된 프로젝트와 PortfolioDto 형식으로 변환합니다.
     *
     * @param member      ResponseDTO로 변환할 멤버 객체
     * @param archivePage
     * @param customUserDetails inviteAccessService에서 사용하기 위한 객체
     * @return PortfolioDto 객체
     */
    private PortfolioDto toPortfolioDto(MemberEntity member, Page<Archive> archivePage, CustomUserDetails customUserDetails) {
        // ArchivePageResponseDTO 생성
        ArchivePageResponseDTO archivePageResponseDTO = archiveService.createArchivePageResponse(archivePage, member.getUsername());

        List<InviteProjectOutputDTO> acceptProjectOutputDTOList = inviteAccessService.getInvitedProjects(customUserDetails);

        // CareerHistoryDto 반환, 포트폴리오는 별도로 설정
        return new PortfolioDto(
                archivePageResponseDTO,
                acceptProjectOutputDTOList
        );
    }

    /**
     * 멤버의 활동 정보를 TeamFeedbackDto 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return TeamFeedbackDto 객체
     */
    public TeamFeedbackDto toTeamFeedbackDto(MemberEntity member) {
        MyPageEvaluationDTO myPageEvaluationDTO = myPageEvaluationService.getMyEvaluation(member.getNickname());
        return new TeamFeedbackDto(
                myPageEvaluationDTO
        );
    }

    /**
     * 멤버의 활동 정보를 MyActivitiesDto 형식으로 변환합니다.
     *
     * @param member ResponseDTO로 변환할 멤버 객체
     * @return MyActivitiesDto 객체
     */
    public MyActivitiesDto getMyActivity(MemberEntity member) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDateTime");

        // archive 조회
        List<Archive> archiveWriteList = archiveRepository.findByWriter(member, sort);
        List<ArchiveBookmark> archiveBookmarkList = archiveBookmarkRepository.findAllByUserId(member, sort);

        // PostDTO 리스트로 변환
        List<MyActivitiesDto.PostDTO> archiveWrittenPosts = archiveWriteList.stream()
                .map(this::mapToPostDTO)
                .collect(Collectors.toList());
        List<MyActivitiesDto.PostDTO> archiveBookmarkedPosts = archiveBookmarkList.stream()
                .map(archiveBookmark -> mapToPostDTO(archiveBookmark.getArchive()))
                .collect(Collectors.toList());

        // MyArchiveDTO 생성
        MyActivitiesDto.MyArchiveDTO myArchiveDTO = new MyActivitiesDto.MyArchiveDTO(archiveWrittenPosts, archiveBookmarkedPosts);

        // project 조회
        List<Project> projectWriteList = projectRepository.findByWriter(member, sort);
        List<ProjectBookmark> projectBookmarkList = projectBookmarkRepository.findAllByMember(member, sort);

        // PostDTO 리스트로 변환
        List<MyActivitiesDto.PostDTO> projectWrittenPosts = projectWriteList.stream()
                .map(this::mapToPostDTO)
                .collect(Collectors.toList());
        List<MyActivitiesDto.PostDTO> projectBookmarkedPosts = projectBookmarkList.stream()
                .map(projectBookmark -> mapToPostDTO(projectBookmark.getProject()))
                .collect(Collectors.toList());

        // MyProjectDTO 생성
        MyActivitiesDto.MyProjectDTO myProjectDTO = new MyActivitiesDto.MyProjectDTO(projectWrittenPosts, projectBookmarkedPosts);

        // MyActivitiesDto 생성 후 반환
        return new MyActivitiesDto(myProjectDTO, myArchiveDTO);
    }

    // Archive 객체를 PostDTO로 변환
    private MyActivitiesDto.PostDTO mapToPostDTO(Archive archive) {
        return new MyActivitiesDto.PostDTO(
                archive.getTitle(),
                archive.getId(),
                Date.from(archive.getCreatedDateTime().atZone(ZoneId.systemDefault()).toInstant()), // LocalDateTime → Date 변환
                archive.getComments().size() // 댓글 수
        );
    }

    // Project 객체를 PostDTO로 변환
    private MyActivitiesDto.PostDTO mapToPostDTO(Project project) {
        return new MyActivitiesDto.PostDTO(
                project.getTitle(),
                project.getId(),
                Date.from(project.getCreatedDateTime().atZone(ZoneId.systemDefault()).toInstant()), // LocalDateTime → Date 변환
                project.getComments().size() // 댓글 수
        );
    }
}
