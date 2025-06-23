package sequence.sequence_member.project.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.project.dto.CommentOutputDTO;
import sequence.sequence_member.project.dto.ProjectMemberOutputDTO;
import sequence.sequence_member.project.dto.ProjectOutputDTO;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectGetService {

    private final ProjectRepository projectRepository;
    private final ProjectViewService projectViewService;
    private final CommentService commentService;
    private final ProjectBookmarkService projectBookmarkService;
    private final ProjectMemberService projectMemberService;

    /**
     * Project를 조회하는 메인 로직 함수
     * @param projectId
     * @return
     */
    @Transactional(readOnly = true)
    public ProjectOutputDTO getProject(Long projectId, HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));

        List<ProjectMemberOutputDTO> projectMemberOutputDTOS = projectMemberService.getProjectMemberOutputDTOS(project);

        List<CommentOutputDTO> commentOutputDTOS = commentService.getCommentOutputDTOS(project);

        int views = projectViewService.getViews(projectId, request, project);

        boolean bookmarked = projectBookmarkService.isBookmarked(projectId, customUserDetails);

        return ProjectOutputDTO.from(project,projectMemberOutputDTOS, commentOutputDTOS, views, bookmarked);
    }
}
