package sequence.sequence_member.project.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectInvitedMemberRepository projectInvitedMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberService projectMemberService;
    private final MemberRepository memberRepository;
    private final ProjectViewService projectViewService;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final ProjectBookmarkService projectBookmarkService;

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
        MemberEntity writer = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("요청하는 유저가 존재하지 않습니다."));
        if (!project.getWriter().equals(writer)) {
            throw new AuthException("작성자만 수정할 수 있습니다.");
        }
        // Project Entity에 ProjectInputDTO의 정보를 업데이트
        project.updateProject(projectUpdateDTO);

        // 삭제된 멤버들은 ProjectMember에서 삭제
        List<MemberEntity> deletedMembers = memberRepository.findByNicknameIn(projectUpdateDTO.getDeletedMembersNicknames());
        for(MemberEntity deletedMember : deletedMembers){
            ProjectMember projectMember = projectMemberRepository.findByMemberIdAndProjectId(
                    deletedMember.getId(), projectId);
            if(projectMember==null){
                log.error("삭제된 멤버가 프로젝트에 존재하지 않습니다.");
                continue;
            }
            if(deletedMembers.contains(writer)){
                throw new BAD_REQUEST_EXCEPTION("작성자는 멤버에서 삭제할 수 없습니다.");
            }
            projectMember.softDelete(customUserDetails.getUsername());
            projectMemberRepository.save(projectMember);
        }

        // 만약 초대목록 멤버에 존재한다면 본인은 제거
        if (projectUpdateDTO.getInvitedMembersNicknames() != null) {
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

        projectMemberService.saveProjectInvitedMemberEntities(project, invitedMembers);

        return getProject(projectId,request, customUserDetails);
    }

    // soft delete를 통해 프로젝트를 삭제하는 메인 로직 함수
    public void deleteProject(Long projectId, CustomUserDetails customUserDetails){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
        MemberEntity writer = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        if (!project.getWriter().equals(writer)) {
            throw new AuthException("작성자만 삭제할 수 있습니다.");
        }
        project.softDelete(customUserDetails.getUsername());

        //북마크 삭제
        projectBookmarkService.deleteByProject(project, customUserDetails.getUsername());

        //ProjectInvitedMember 삭제
        deleteProjectInvitedMember(project, project.getInvitedMembers(), customUserDetails.getUsername());

        //ProjectMember 삭제
        deleteProjectMembers(project, project.getMembers(), customUserDetails.getUsername());

        projectRepository.save(project);
    }

    private void deleteProjectInvitedMember(Project project, List<ProjectInvitedMember> invitedMembers,
                                            String username) {
        for (ProjectInvitedMember invitedMember : invitedMembers) {
            invitedMember.softDelete(username);
        }
        projectInvitedMemberRepository.saveAll(invitedMembers);
    }

    private void deleteProjectMembers(Project project, List<ProjectMember> projectMembers, String username){
        for(ProjectMember member : projectMembers){
            member.softDelete(username);
        }
        projectMemberRepository.saveAll(projectMembers);
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
            default -> 12;
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

    public Page<ProjectFilterOutputDTO> getProjectsBySearch(String title, String sortBy, int page, int size){
        // 페이지 크기 제한
        int pageSize = switch (size) {
            case 30 -> 30;
            case 50 -> 50;
            default -> 12;
        };

        // 정렬 조건 설정
        Sort sort = Sort.by(
            "createdDateTime".equals(sortBy) ? "createdDateTime" : "modifiedDateTime"
        ).descending();

        //pageable 객체 만들기
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Project> projects = projectRepository.findProjectsByFilterdSearch(title, pageable);

        return projects.map(ProjectFilterOutputDTO::toProjectFilterOutputDTO);
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




}
