package sequence.sequence_member.project.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.dto.ProjectMemberOutputDTO;
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

    // 프로젝트 초대 멤버를 저장하는 함수
    @Transactional
    public void saveProjectInvitedMember(ProjectInputDTO projectInputDTO, MemberEntity writer, Project project) {
        if(projectInputDTO.getInvitedMembersNicknames()==null || projectInputDTO.getInvitedMembersNicknames().isEmpty()){
            return;
        }
        projectInputDTO.getInvitedMembersNicknames().remove(writer.getNickname()); // 본인은 제거
        List<MemberEntity> invitedMembers = memberRepository.findByNicknameIn(projectInputDTO.getInvitedMembersNicknames());
        saveProjectInvitedMemberEntities(project, invitedMembers);
    }

    // 초대된 멤버들을 저장하는 함수
    public void saveProjectInvitedMemberEntities(Project project, List<MemberEntity> invitedMembers){
        for(MemberEntity member : invitedMembers){
            ProjectInvitedMember entity = ProjectInvitedMember.fromProjectAndMember(project,member);
            projectInvitedMemberRepository.save(entity);
        }
    }

    // 초대 완료까지된 멤버를 저장하는 함수
    public void saveProjectMember(Project project, MemberEntity member) {
        ProjectMember entity = ProjectMember.fromProjectAndMember(project,member);
        projectMemberRepository.save(entity);
    }

    // 프로젝트 멤버 정보를 조회하는 함수
    @NotNull
    public List<ProjectMemberOutputDTO> getProjectMemberOutputDTOS(Project project) {
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

}
