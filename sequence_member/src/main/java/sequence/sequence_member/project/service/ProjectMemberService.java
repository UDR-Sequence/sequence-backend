package sequence.sequence_member.project.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;
    private final ProjectInvitedMemberRepository projectInvitedMemberRepository;
    private final ProjectInviteEmailService projectInviteEmailService;

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

            // 초대 이메일 발송
            projectInviteEmailService.sendInviteEmail(project, member);
            log.info("프로젝트 초대 이메일 발송 - 프로젝트: {}, 멤버: {}", project.getProjectName(), member.getNickname());
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

    // 프로젝트 수정 시 멤버들에게 알림 이메일 발송
    @Transactional(readOnly = true)
    public void notifyProjectUpdate(Project project, String updateDetails) {
        try {
            List<ProjectMember> projectMembers = project.getMembers();

            if (projectMembers != null && !projectMembers.isEmpty()) {
                // MemberEntity 리스트로 변환
                List<MemberEntity> memberList = projectMembers.stream()
                        .map(ProjectMember::getMember)
                        .filter(member -> member != null)
                        .toList();

                // 리스트로 전달 (현재 메서드 시그니처에 맞게)
                projectInviteEmailService.sendProjectUpdateEmail(project, memberList, updateDetails);

                log.info("프로젝트 수정 알림 발송 완료 - 프로젝트: {}, 대상자: {}명",
                        project.getProjectName(), memberList.size());
            }

        } catch (Exception e) {
            log.error("프로젝트 수정 알림 실패 - 프로젝트: {}, 오류: {}",
                    project.getProjectName(), e.getMessage());
        }
    }

}
