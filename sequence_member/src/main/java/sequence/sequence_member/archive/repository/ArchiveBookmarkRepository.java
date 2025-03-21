package sequence.sequence_member.archive.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveBookmark;
import sequence.sequence_member.member.entity.MemberEntity;


@Repository
public interface ArchiveBookmarkRepository extends JpaRepository<ArchiveBookmark, Long> {
    Optional<ArchiveBookmark> findByArchiveAndUserId(Archive archive, MemberEntity userId);
    boolean existsByArchiveAndUserId(Archive archive, MemberEntity userId);
    List<ArchiveBookmark> findAllByUserId(MemberEntity userId);  // 특정 사용자의 북마크 목록 조회
    List<ArchiveBookmark> findAllByUserId(MemberEntity userId, Sort sort);  // 정렬 추가, 특정 사용자의 북마크 목록 조회
    long countByArchive(Archive archive);  // 특정 아카이브의 북마크 수를 반환하는 메서드

    void deleteByArchiveId(Long archiveId);
} 