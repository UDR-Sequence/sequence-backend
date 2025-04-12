package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveBookmark;
import sequence.sequence_member.archive.repository.ArchiveBookmarkRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.global.exception.CanNotFindResourceException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveBookmarkService {
    private final ArchiveBookmarkRepository bookmarkRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;

    // 아카이브 존재 여부 확인
    public boolean checkArchiveExists(Long archiveId) {
        return archiveRepository.existsById(archiveId);
    }

    // 북마크 토글(추가/삭제)
    @Transactional
    public boolean toggleBookmark(Long archiveId, String username) {
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new CanNotFindResourceException("아카이브를 찾을 수 없습니다."));
        
        MemberEntity userId = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new UserNotFindException("회원을 찾을 수 없습니다."));

        Optional<ArchiveBookmark> bookmark = bookmarkRepository.findByArchiveAndUserId(archive, userId);
        
        if (bookmark.isPresent()) {
            // 북마크 취소
            bookmarkRepository.delete(bookmark.get());
            return false;
        } else {
            // 북마크 추가
            bookmarkRepository.save(ArchiveBookmark.builder()
                .archive(archive)
                .userId(userId)
                .build());
            return true;
        }
    }

    // 사용자의 북마크 목록 조회
    public List<Long> getUserBookmarks(String username) {
        MemberEntity userId = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new UserNotFindException("회원을 찾을 수 없습니다."));
        
        return bookmarkRepository.findAllByUserId(userId).stream()
            .map(bookmark -> bookmark.getArchive().getId())
            .collect(Collectors.toList());
    }

    // 특정 아카이브의 북마크 여부 확인
    public boolean isBookmarked(Long archiveId, String username) {
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new CanNotFindResourceException("아카이브를 찾을 수 없습니다."));
        
        MemberEntity userId = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new UserNotFindException("회원을 찾을 수 없습니다."));
        
        return bookmarkRepository.existsByArchiveAndUserId(archive, userId);
    }
} 