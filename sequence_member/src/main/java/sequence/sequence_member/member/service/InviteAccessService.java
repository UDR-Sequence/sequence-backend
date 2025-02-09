package sequence.sequence_member.member.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.BadRequestExeption;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.AcceptProjectOutputDTO;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteProjectOutputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
import sequence.sequence_member.project.entity.ProjectMember;
import sequence.sequence_member.project.repository.ProjectInvitedMemberRepository;
import sequence.sequence_member.project.repository.ProjectMemberRepository;

@Service
@RequiredArgsConstructor
public class InviteAccessService {

    private final ProjectInvitedMemberRepository projectInvitedMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MemberRepository memberRepository;

    /**
     * 유저가 초대받은 프로젝트 목록을 조회하는 메인 로직 함수
     * @param customUserDetails
     * @return
     */
    @Transactional(readOnly = true)
    public List<InviteProjectOutputDTO> getInvitedProjects(CustomUserDetails customUserDetails){
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        List<ProjectInvitedMember> inviteList = projectInvitedMemberRepository.findByMemberId(member.getId());
        List<InviteProjectOutputDTO> inviteProjectOutputDTOList = new ArrayList<>();
        for(ProjectInvitedMember detail : inviteList ){
            inviteProjectOutputDTOList.add(InviteProjectOutputDTO.builder()
                    .projectInvitedMemberId(detail.getId())
                    .title(detail.getProject().getTitle())
                    .writer(detail.getProject().getWriter().getNickname())
                    .inviteDate(Date.valueOf(detail.getCreatedDateTime().toLocalDate()))
                    .build());
        }
        return inviteProjectOutputDTOList;
    }

    /**
     * 유저가 초대받은 프로젝트에 승인하는 메인 로직 함수
     * @param customUserDetails
     * @param projectInvitedMemberId
     */
    @Transactional
    public void acceptInvite(CustomUserDetails customUserDetails, Long projectInvitedMemberId){
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(() -> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        ProjectInvitedMember projectInvitedMember = projectInvitedMemberRepository.findByIdAndMemberId(projectInvitedMemberId,member.getId()).orElseThrow(() -> new BadRequestExeption("해당 프로젝트 초대가 유효하지 않습니다."));

        projectMemberRepository.save(ProjectMember.builder()
                .member(member)
                .project(projectInvitedMember.getProject())
                .build());

        projectInvitedMemberRepository.delete(projectInvitedMember);
    }

    @Transactional
    public void rejectInvite(CustomUserDetails customUserDetails, Long projectInvitedMemberId){
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(() -> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        ProjectInvitedMember projectInvitedMember = projectInvitedMemberRepository.findByIdAndMemberId(projectInvitedMemberId,member.getId()).orElseThrow(() -> new BadRequestExeption("해당 프로젝트 초대가 유효하지 않습니다."));
        projectInvitedMemberRepository.delete(projectInvitedMember);
    }

    /**
     * 유저가 승인한(참여하는) 프로젝트 목록을 조회하는 메인 로직 함수
     * @param customUserDetails
     * @return
     */
    public List<AcceptProjectOutputDTO> getAcceptedProjects(CustomUserDetails customUserDetails){
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        List<ProjectMember> projectMembers = projectMemberRepository.findByMemberId(member.getId());
        List<AcceptProjectOutputDTO> acceptProjectOutputDTOList = new ArrayList<>();
        for(ProjectMember detail : projectMembers ){
            acceptProjectOutputDTOList.add(AcceptProjectOutputDTO.builder()
                    .projectId(detail.getProject().getId())
                    .title(detail.getProject().getTitle())
                    .writer(detail.getProject().getWriter().getNickname())
                    .build());
        }
        return acceptProjectOutputDTOList;
    }
}
