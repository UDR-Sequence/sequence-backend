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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveBookmarkService {
    private final ArchiveBookmarkRepository bookmarkRepository;
    private final ArchiveRepository archiveRepository;

    // 아카이브 존재 여부 확인
    public boolean checkArchiveExists(Long archiveId) {
        return archiveRepository.existsById(archiveId);
    }

    // 북마크 토글(추가/삭제)
    @Transactional
    public boolean toggleBookmark(Long archiveId, String username) {
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();

        Optional<ArchiveBookmark> bookmark = bookmarkRepository.findByArchiveAndUsername(archive, username);
        
        if (bookmark.isPresent()) {
            // 북마크 취소
            bookmarkRepository.delete(bookmark.get());
            return false;
        } else {
            // 북마크 추가
            bookmarkRepository.save(ArchiveBookmark.builder()
                .archive(archive)
                .username(username)
                .build());
            return true;
        }
    }

    // 사용자의 북마크 목록 조회
    public List<Long> getUserBookmarks(String username) {
        return bookmarkRepository.findAllByUsername(username).stream()
            .map(bookmark -> bookmark.getArchive().getId())
            .collect(Collectors.toList());
    }

    // 특정 아카이브의 북마크 여부 확인
    public boolean isBookmarked(Long archiveId, String username) {
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();
        return bookmarkRepository.existsByArchiveAndUsername(archive, username);
    }
} 