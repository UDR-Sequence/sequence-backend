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
import sequence.sequence_member.archive.entity.ArchiveBookmark;
import sequence.sequence_member.archive.repository.ArchiveBookmarkRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.exception.AuthException;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.mypage.dto.*;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectBookmark;
import sequence.sequence_member.project.repository.ProjectBookmarkRepository;
import sequence.sequence_member.project.repository.ProjectRepository;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageUpdateService myPageUpdateService;
    private final MemberRepository memberRepository;
    private final ArchiveRepository archiveRepository;
    private final MyPageMapper myPageMapper;
    private final ProjectRepository projectRepository;
    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final ArchiveBookmarkRepository archiveBookmarkRepository;
    /**
     * 주어진 사용자명(username)에 해당하는 마이페이지 정보를 조회합니다.
     *
     * @param username 조회할 사용자의 이름
     * @param page archive 페이지네이션 파라미터
     * @param size archive 페이지네이션 파라미터
     *
     * @return 사용자의 마이페이지 정보를 담은 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    public MyPageResponseDTO getMyProfile(String username, int page, int size) {
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDateTime").descending());
        Page<Archive> archivePage = archiveRepository.findByArchiveMembers_Member_Id(member.getId(), pageable);

        return myPageMapper.toDTO(member, archivePage);
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
     * 주어진 nickname에 해당하는 마이페이지 정보를 조회합니다.
     *
     * @param nickname 조회할 회원의 nickname
     * @param page archive 페이지네이션 파라미터
     * @param size archive 페이지네이션 파라미터
     *
     * @return 사용자의 마이페이지 정보를 담은 DTO
     * @throws EntityNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    public MyPageResponseDTO getUserProfile(String nickname, int page, int size) {
        MemberEntity member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDateTime").descending());
        Page<Archive> archivePage = archiveRepository.findByArchiveMembers_Member_Id(member.getId(), pageable);

        return myPageMapper.toDTO(member, archivePage);
    }



    public MyActivityResponseDTO getMyActivity(String nickname, CustomUserDetails customUserDetails) {
        MemberEntity member = memberRepository.findByUsername(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("해당 유저가 존재하지 않습니다."));

        if (!member.getNickname().equals(nickname)) {
            throw new AuthException("본인 프로필에서만 조회할 수 있습니다.");
        }

        // archive조회
        MyArchiveDTO myArchiveDTO = new MyArchiveDTO();

        List<PostDTO> archiveWrittenPosts;
        List<PostDTO> archiveBookmarkedPosts;
        MyProjectDTO myProjectDTO = new MyProjectDTO();
        List<PostDTO> projectWrittenPosts;
        List<PostDTO> projectBookmarkedPosts;

        return new MyActivityResponseDTO(myProjectDTO,myArchiveDTO);
    }

    private PostDTO mapToPostDTO(Archive archive) {
        return new PostDTO(
                archive.getTitle(),
                "archive",
                archive.getId(),
                Date.from(archive.getCreatedDateTime().atZone(ZoneId.systemDefault()).toInstant()), // LocalDateTime → Date 변환
                archive.getComments().size() // 댓글 수
        );
    }

    private PostDTO mapToPostDTO(Project project) {
        return new PostDTO(
                project.getTitle(),
                "project",
                project.getId(),
                Date.from(project.getCreatedDateTime().atZone(ZoneId.systemDefault()).toInstant()), // LocalDateTime → Date 변환
                project.getComments().size()
        );
    }
}
