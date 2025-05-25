package sequence.sequence_member.project.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.CommentDTO;
import sequence.sequence_member.project.dto.CommentOutputDTO;
import sequence.sequence_member.project.dto.ProjectMemberOutputDTO;
import sequence.sequence_member.project.dto.ProjectOutputDTO;
import sequence.sequence_member.project.entity.Comment;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectMember;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectGetService {

    private final ProjectRepository projectRepository;
    private final ProjectViewService projectViewService;
    private final MemberRepository memberRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;

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

    // 프로젝트 북마크 여부 확인 ( 북마크 되어있으면 true, 아니면 false, 로그인 안한 사용자는 false)
    public boolean isBookmarked(Long projectId, CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return false;
        }
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername()).orElse(null);
        if (member == null) {
            return false;
        }
        return projectBookmarkRepository.existsByMemberIdAndProjectId(member.getId(), projectId);
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
                    .profileImage(comment.getWriter().getProfileImg())
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
                        .profileImage(comment.getWriter().getProfileImg())
                        .build();
                commentOutputDTO.addChildComment(childCommentDTO);
            }
            commentOutputDTOS.add(commentOutputDTO);
        }
        return commentOutputDTOS;
    }

}
