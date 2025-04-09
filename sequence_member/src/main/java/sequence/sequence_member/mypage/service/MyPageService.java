package sequence.sequence_member.mypage.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.mypage.dto.*;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
import sequence.sequence_member.project.repository.CommentRepository;
import sequence.sequence_member.project.repository.ProjectInvitedMemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MyPageUpdateService myPageUpdateService;
    private final MemberRepository memberRepository;
    private final ArchiveRepository archiveRepository;
    private final MyPageMapper myPageMapper;
    private final ProjectInvitedMemberRepository projectInvitedMemberRepository;
    private final CommentRepository commentRepository;

    /**
     * 주어진 사용자명(username)에 해당하는 마이페이지 정보를 조회합니다.
     *
     * @param username 조회할 사용자의 이름
     * @param page archive 페이지네이션 파라미터
     * @param size archive 페이지네이션 파라미터
     * @param customUserDetails 포트폴리오 객체에서 사용하는 파라미터
     *
     * @return 사용자의 마이페이지 정보를 담은 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    public MyPageResponseDTO getMyProfile(String username, int page, int size, CustomUserDetails customUserDetails) {
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDateTime").descending());
        Page<Archive> archivePage = archiveRepository.findByWriter(member, pageable);

        List<InvitedProjectWithCommentDTO> invitedProjects = getInvitedProjects(customUserDetails);

        return myPageMapper.toMyPageResponseDto(member, archivePage, invitedProjects);

    }

    /**
     * 사용자 마이페이지 정보를 업데이트하는 메서드입니다.
     *
     * @param myPageDTO 업데이트할 마이페이지 정보,
     * @param username 업데이트할 유저의 이름
     *
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    @Transactional
    public void updateMyProfile(
            MyPageRequestDTO myPageDTO, String username,
            MultipartFile authImgFile, List<MultipartFile> portfolios
    ) {
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));
      
        if (!Objects.equals(myPageDTO.getUsername(), username)) {
            throw new IllegalArgumentException("아이디는 변경할 수 없습니다.");
        }

        myPageUpdateService.updateProfile(member, myPageDTO, authImgFile, portfolios);
    }

    /**
     * 주어진 사용자 정보를 기반으로 해당 사용자가 초대받은 프로젝트 목록과
     * 각 프로젝트의 댓글 수를 포함한 정보를 조회합니다.
     *
     * @param customUserDetails 현재 인증된 사용자의 정보
     * @return 초대받은 프로젝트 정보와 각 프로젝트의 댓글 수를 담은 DTO 리스트
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    public MyPageResponseDTO getUserProfile(String nickname, int page, int size, CustomUserDetails customUserDetails) {
        MemberEntity member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDateTime").descending());
        Page<Archive> archivePage = archiveRepository.findByWriter(member, pageable);

        List<InvitedProjectWithCommentDTO> invitedProjects = getInvitedProjects(customUserDetails);

        return myPageMapper.toMyPageResponseDto(member, archivePage, invitedProjects);
    }

    /**
     * 유저가 초대받은 프로젝트 목록을 조회하는 메인 로직 함수
     * @param customUserDetails
     * @return
     */
    @Transactional
    public List<InvitedProjectWithCommentDTO> getInvitedProjects(CustomUserDetails customUserDetails) {
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("해당 유저가 존재하지 않습니다."));

        List<ProjectInvitedMember> inviteList = projectInvitedMemberRepository.findByMemberId(member.getId());

        List<InvitedProjectWithCommentDTO> result = new ArrayList<>();

        for (ProjectInvitedMember detail : inviteList) {
            Long projectId = detail.getProject().getId();
            int commentCount = commentRepository.countByProjectId(projectId);   // 댓글수 가져오기

            result.add(InvitedProjectWithCommentDTO.builder()
                    .projectInvitedMemberId(detail.getProject().getId())
                    .title(detail.getProject().getTitle())
                    .writer(detail.getProject().getWriter().getNickname())
                    .inviteDate(detail.getCreatedDateTime().toLocalDate())
                    .commentCount(commentCount)
                    .build());
        }

        return result;
    }
}
