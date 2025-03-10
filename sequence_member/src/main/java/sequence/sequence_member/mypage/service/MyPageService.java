package sequence.sequence_member.mypage.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.mypage.dto.MyPageMapper;
import sequence.sequence_member.mypage.dto.MyPageRequestDTO;
import sequence.sequence_member.mypage.dto.MyPageResponseDTO;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageUpdateService myPageUpdateService;
    private final MemberRepository memberRepository;
    private final ArchiveRepository archiveRepository;
    private final MyPageMapper myPageMapper;

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
    public void updateMyProfile(MyPageRequestDTO myPageDTO, String username) {
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if (!Objects.equals(myPageDTO.getUsername(), username)) {
            throw new IllegalArgumentException("아이디는 변경할 수 없습니다.");
        }

        myPageUpdateService.updateProfile(member, myPageDTO);
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
}
