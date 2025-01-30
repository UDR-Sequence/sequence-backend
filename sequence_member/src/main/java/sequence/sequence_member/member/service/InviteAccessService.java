package sequence.sequence_member.member.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteOutputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
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
    public List<InviteOutputDTO> getInvitedProjects(CustomUserDetails customUserDetails){
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername()).orElseThrow(()-> new UserNotFindException("해당 유저가 존재하지 않습니다."));
        List<ProjectInvitedMember> inviteList = projectInvitedMemberRepository.findByMemberId(member.getId());
        List<InviteOutputDTO> inviteOutputDTOList = new ArrayList<>();
        for(ProjectInvitedMember detail : inviteList ){
            inviteOutputDTOList.add(InviteOutputDTO.builder()
                    .projectId(detail.getProject().getId())
                    .title(detail.getProject().getTitle())
                    .writer(detail.getProject().getWriter().getNickname())
                    .inviteDate(Date.valueOf(detail.getCreatedDateTime().toLocalDate()))
                    .build());
        }
        return inviteOutputDTOList;
    }
}
