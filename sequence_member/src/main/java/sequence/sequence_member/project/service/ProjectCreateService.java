package sequence.sequence_member.project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectCreateService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberService projectMemberService;
    private final MemberRepository memberRepository;

    @Transactional
    public void createProject(ProjectInputDTO projectInputDTO, String username){
        MemberEntity writer = memberRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        Project project = projectRepository.save(Project.fromProjectInput(projectInputDTO,writer));

        projectMemberService.saveProjectInvitedMember(projectInputDTO, writer, project);
        projectMemberService.saveProjectMember(project, writer);
    }

}
