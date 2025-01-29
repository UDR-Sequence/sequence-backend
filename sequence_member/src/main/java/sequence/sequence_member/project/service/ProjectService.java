package sequence.sequence_member.project.service;

import jakarta.transaction.Transactional;
import java.lang.reflect.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.UserNotFoundException;
import sequence.sequence_member.global.utils.DataConvertor;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
import sequence.sequence_member.project.entity.ProjectMember;
import sequence.sequence_member.project.repository.ProjectInvitedMemberEntityRepository;
import sequence.sequence_member.project.repository.ProjectMemberEntityRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectInvitedMemberEntityRepository projectInvitedMemberEntityRepository;
    private final ProjectMemberEntityRepository projectMemberEntityRepository;
    private final MemberRepository memberRepository;

    // Project를 생성하는 메인 로직 함수
    @Transactional
    public void createProject(ProjectInputDTO projectInputDTO, CustomUserDetails customUserDetails){
        MemberEntity memberEntity = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFoundException("해당 유저가 존재하지 않습니다."));
        Project project = saveProjectEntity(projectInputDTO,memberEntity);
        List<MemberEntity> invitedMembers = memberRepository.findByUsernameIn(projectInputDTO.getInvitedMembers());
        saveProjectInvitedMemberEntities(project, invitedMembers);
        savePrjectMemberEntity(project, memberEntity);
    }

    // Project를 조회하는 메인 로직 함수


    // Project를 저장 및 반환하는 함수
    private Project saveProjectEntity(ProjectInputDTO projectInputDTO, MemberEntity memberEntity){
        return projectRepository.save(Project.builder()
                .title(projectInputDTO.getTitle())
                .period(projectInputDTO.getPeriod())
                .category(projectInputDTO.getCategory())
                .personnel(projectInputDTO.getPersonnel())
                .roles(DataConvertor.listToString(projectInputDTO.getRoles()))
                .skills(DataConvertor.listToString(projectInputDTO.getSkills()))
                .meetingOption(projectInputDTO.getMeetingOption())
                .step(projectInputDTO.getStep())
                .introduce(projectInputDTO.getIntroduce())
                .article(projectInputDTO.getArticle())
                .link(projectInputDTO.getLink())
                .writer(memberEntity)
                .build());
    }

    // 초대된 멤버들을 저장하는 함수
    private void saveProjectInvitedMemberEntities(Project project, List<MemberEntity> invitedMembers){
        for(MemberEntity member : invitedMembers){
            ProjectInvitedMember entity = ProjectInvitedMember.builder()
                    .member(member)
                    .project(project)
                    .build();
            projectInvitedMemberEntityRepository.save(entity);
        }
    }

    // 초대 완료까지된 멤버를 저장하는 함수. 프로젝트 생성 시점에는 writer만 저장
    private void savePrjectMemberEntity(Project project, MemberEntity writer) {
        ProjectMember entity = ProjectMember.builder()
                .member(writer)
                .project(project)
                .build();
        projectMemberEntityRepository.save(entity);
    }
}
