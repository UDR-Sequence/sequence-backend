package sequence.sequence_member.project.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
import sequence.sequence_member.project.entity.ProjectMember;
import sequence.sequence_member.project.repository.ProjectInvitedMemberRepository;
import sequence.sequence_member.project.repository.ProjectMemberRepository;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final ProjectInvitedMemberRepository projectInvitedMemberRepository;

    public void saveProjectInvitedMember(ProjectInputDTO projectInputDTO, MemberEntity writer, Project project) {
        if(projectInputDTO.getInvitedMembersNicknames()==null || projectInputDTO.getInvitedMembersNicknames().isEmpty()){
            return;
        }
        projectInputDTO.getInvitedMembersNicknames().remove(writer.getNickname()); // 본인은 제거
        List<MemberEntity> invitedMembers = memberRepository.findByNicknameIn(projectInputDTO.getInvitedMembersNicknames());
        saveProjectInvitedMemberEntities(project, invitedMembers);
    }

    // 초대 완료까지된 멤버를 저장하는 함수. 프로젝트 생성 시점에는 writer만 저장
    public void saveProjectMember(Project project, MemberEntity writer) {
        ProjectMember entity = ProjectMember.fromProjectAndMember(project,writer);
        projectMemberRepository.save(entity);
    }

    // 초대된 멤버들을 저장하는 함수
    public void saveProjectInvitedMemberEntities(Project project, List<MemberEntity> invitedMembers){
        for(MemberEntity member : invitedMembers){
            ProjectInvitedMember entity = ProjectInvitedMember.fromProjectAndMember(project,member);
            projectInvitedMemberRepository.save(entity);
        }
    }
}
