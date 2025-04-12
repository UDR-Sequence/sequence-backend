package sequence.sequence_member.project.service;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.MeetingOption;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Step;
import sequence.sequence_member.global.exception.AuthException;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.global.utils.DataConvertor;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.*;
import sequence.sequence_member.project.entity.Comment;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
import sequence.sequence_member.project.entity.ProjectMember;
import sequence.sequence_member.project.mapper.PeriodMapper;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectInvitedMemberRepository;
import sequence.sequence_member.project.repository.ProjectMemberRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectInvitedMemberRepository projectInvitedMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final ProjectViewService projectViewService;
    private final ProjectBookmarkRepository projectBookmarkRepository;

    @Transactional
    public void createProject(ProjectInputDTO projectInputDTO, String username){
        MemberEntity writer = memberRepository.findByUsername(username).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        Project project = projectRepository.save(Project.fromProjectInput(projectInputDTO,writer));

        saveProjectInvitedMember(projectInputDTO, writer, project);
        saveProjectMember(project, writer);
    }

    private void saveProjectInvitedMember(ProjectInputDTO projectInputDTO, MemberEntity writer, Project project) {
        if(projectInputDTO.getInvitedMembersNicknames()==null || projectInputDTO.getInvitedMembersNicknames().isEmpty()){
            return;
        }
        projectInputDTO.getInvitedMembersNicknames().remove(writer.getNickname()); // 본인은 제거
        List<MemberEntity> invitedMembers = memberRepository.findByNicknameIn(projectInputDTO.getInvitedMembersNicknames());
        saveProjectInvitedMemberEntities(project, invitedMembers);
    }

    // 초대 완료까지된 멤버를 저장하는 함수. 프로젝트 생성 시점에는 writer만 저장
    private void saveProjectMember(Project project, MemberEntity writer) {
        ProjectMember entity = ProjectMember.fromProjectAndMember(project,writer);
        projectMemberRepository.save(entity);
    }

    /**
     * Project를 조회하는 메인 로직 함수
      * @param projectId
     * @return
     */
    @Transactional(readOnly = true)
    public ProjectOutputDTO getProject(Long projectId, HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));

        List<ProjectMemberOutputDTO> projectMemberOutputDTOS = getProjectMemberOutputDTOS(project);

        List<CommentOutputDTO> commentOutputDTOS = getCommentOutputDTOS(project);

        int views = getViews(projectId, request, project);
        boolean bookmarked = isBookmarked(projectId, customUserDetails);
        return ProjectOutputDTO.from(project,projectMemberOutputDTOS, commentOutputDTOS, views, bookmarked);
    }

    private int getViews(Long projectId, HttpServletRequest request, Project project) {
        //views 조회
        int views = 0;
        try {
            views = projectViewService.getViewsFromRedis(request, projectId);
        }catch (Exception e){
            views = project.getViews()+1;
        }
        return views;
    }

    @NotNull
    private static List<CommentOutputDTO> getCommentOutputDTOS(Project project) {
        // 댓글들을 조회하여 응답데이터에 포함함
        List<Comment> comments = project.getComments();
        List<CommentOutputDTO> commentOutputDTOS = new ArrayList<>(); // Comments를 정리하여 CommentOutputDTO로 변환
        for(Comment comment : comments){
            if(comment.getParentComment()!=null){
                continue;
            }

            // 부모 댓글을 CommentDTO로 변환
            CommentDTO parentComment = CommentDTO.builder()
                    .id(comment.getId())
                    .writer(comment.getWriter().getNickname())
                    .content(comment.getContent())
                    .createdLocalDateTime(comment.getCreatedDateTime())
                    .build();

            CommentOutputDTO commentOutputDTO = new CommentOutputDTO(parentComment);

            // 자식 댓글을 CommentDTO로 변환후 CommentOutputDTO의 childComments에 추가
            List<Comment> childComments = comment.getChildComments();
            for(Comment childComment : childComments){
                CommentDTO childCommentDTO = CommentDTO.builder()
                        .id(childComment.getId())
                        .writer(childComment.getWriter().getNickname())
                        .content(childComment.getContent())
                        .createdLocalDateTime(childComment.getCreatedDateTime())
                        .build();
                commentOutputDTO.addChildComment(childCommentDTO);
            }
            commentOutputDTOS.add(commentOutputDTO);
        }
        return commentOutputDTOS;
    }

    @NotNull
    private static List<ProjectMemberOutputDTO> getProjectMemberOutputDTOS(Project project) {
        //Member정보중 memberId, nickname, profileImg만을 추출하여 응답데이터에 포함함
        List<ProjectMember> projectMembers = project.getMembers();
        List<ProjectMemberOutputDTO> projectMemberOutputDTOS = new ArrayList<>();
        for (ProjectMember projectMember : projectMembers) {
            projectMemberOutputDTOS.add(ProjectMemberOutputDTO.builder()
                    .nickname(projectMember.getMember().getNickname())
                    .profileImgUrl(projectMember.getMember().getProfileImg())
                    .build());
        }

        return projectMemberOutputDTOS;

    }

    /**
     * 프로젝트 수정하는 메인로직 함수
     * @param projectId
     * @param customUserDetails
     * @param projectUpdateDTO
     * @return
     */
    @Transactional
    public ProjectOutputDTO updateProject(Long projectId, CustomUserDetails customUserDetails, ProjectUpdateDTO projectUpdateDTO,HttpServletRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
        MemberEntity writer = memberRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("요청하는 유저가 존재하지 않습니다."));
        if (!project.getWriter().equals(writer)) {
            throw new AuthException("작성자만 수정할 수 있습니다.");
        }
        // Project Entity에 ProjectInputDTO의 정보를 업데이트
        project.updateProject(projectUpdateDTO);

        // 삭제된 멤버들은 ProjectMember에서 삭제
        List<MemberEntity> deletedMembers = memberRepository.findByNicknameIn(projectUpdateDTO.getDeletedMembersNicknames());
        if(deletedMembers.contains(writer)){
            throw new BAD_REQUEST_EXCEPTION("작성자는 멤버에서 삭제할 수 없습니다.");
        }
        projectMemberRepository.deleteByProjectAndMemberIn(project, deletedMembers);

        // 만약 초대목록 멤버에 존재한다면 본인은 제거
        if (projectUpdateDTO.getInvitedMembersNicknames() != null &&
            projectUpdateDTO.getInvitedMembersNicknames().contains(writer.getNickname())) {
            projectUpdateDTO.getInvitedMembersNicknames().remove(writer.getNickname());
        }

        // 새롭게 초대된 멤버들은 승인받기 전이므로 ProjectInvitedMember에 저장
        List<MemberEntity> invitedMembers = memberRepository.findByNicknameIn(projectUpdateDTO.getInvitedMembersNicknames());

        // 기존에 초대된 멤버들 조회
        List<MemberEntity> invitedMembersInDB = project.getInvitedMembers().stream()
                .map(ProjectInvitedMember::getMember)
                .toList();

        // 기존에 초대된 상태인 멤버들은 중복으로 초대되지 않도록 제거
        invitedMembers.removeAll(invitedMembersInDB);

        saveProjectInvitedMemberEntities(project, invitedMembers);

        return getProject(projectId,request, customUserDetails);
    }

    public void deleteProject(Long projectId, CustomUserDetails customUserDetails){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
        MemberEntity writer = memberRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        if (!project.getWriter().equals(writer)) {
            throw new AuthException("작성자만 삭제할 수 있습니다.");
        }
        projectRepository.delete(project);
    }


    // 초대된 멤버들을 저장하는 함수
    private void saveProjectInvitedMemberEntities(Project project, List<MemberEntity> invitedMembers){
        for(MemberEntity member : invitedMembers){
            ProjectInvitedMember entity = ProjectInvitedMember.fromProjectAndMember(project,member);
            projectInvitedMemberRepository.save(entity);
        }
    }



    //키워드에 해당하는 프로젝트들을 필터링
    public Page<ProjectFilterOutputDTO> getProjectsByKeywords(Category category,
                                                              String periodKey,
                                                              String roles,
                                                              String skills,
                                                              MeetingOption meetingOption,
                                                              Step step,
                                                              String sortBy,
                                                              int page,
                                                              int size){

        // 페이지 크기 제한
        int pageSize = switch (size) {
            case 30 -> 30;
            case 50 -> 50;
            default -> 10;
        };

        // 정렬 조건 설정
        Sort sort = Sort.by(
            "createdDateTime".equals(sortBy) ? "createdDateTime" : "modifiedDateTime"
        ).descending();

        //pageable 객체 만들기
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Period period = PeriodMapper.PeriodCheck(periodKey);
        Page<Project> projects = projectRepository.findProjectsByFilteredKeywords(category,period,roles,skills,meetingOption,step,pageable);

        return projects.map(ProjectFilterOutputDTO::toProjectFilterOutputDTO);
    }

    public List<ProjectFilterOutputDTO> getProjectsBySearch(String title){
        List<Project> projects = projectRepository.findProjectsByFilterdSearch(title);
        List<ProjectFilterOutputDTO> projectFilterOutputDTOS = new ArrayList<>();

        for(Project project : projects){
            ProjectFilterOutputDTO projectFilterOutputDTO = ProjectFilterOutputDTO.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .writer(project.getWriter().getNickname())
                    .createdDate(project.getCreatedDateTime().toLocalDate())
                    .roles(DataConvertor.stringToList(project.getRoles()))
                    .build();

            projectFilterOutputDTOS.add(projectFilterOutputDTO);
        }

        return projectFilterOutputDTOS;
    }

    public List<ProjectFilterOutputDTO> getAllProjects(){
        List<Project> projects =  projectRepository.findAll();
        List<ProjectFilterOutputDTO> projectFilterOutputDTOS = new ArrayList<>();

        for(Project project : projects){
            ProjectFilterOutputDTO projectFilterOutputDTO = ProjectFilterOutputDTO.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .writer(project.getWriter().getNickname())
                    .createdDate(project.getCreatedDateTime().toLocalDate())
                    .roles(DataConvertor.stringToList(project.getRoles()))
                    .build();

            projectFilterOutputDTOS.add(projectFilterOutputDTO);
        }

        return projectFilterOutputDTOS;

    }


    // 프로젝트 북마크 여부 확인 ( 북마크 되어있으면 true, 아니면 false, 로그인 안한 사용자는 false)
    public boolean isBookmarked(Long projectId, CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return false;
        }
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElse(null);
        if (member == null) {
            return false;
        }
        return projectBookmarkRepository.existsByMemberIdAndProjectId(member.getId(), projectId);
    }

}
