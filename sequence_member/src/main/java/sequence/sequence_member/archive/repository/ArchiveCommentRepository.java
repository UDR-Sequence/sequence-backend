package sequence.sequence_member.archive.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sequence.sequence_member.archive.entity.ArchiveComment;
import sequence.sequence_member.archive.entity.Archive;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchiveCommentRepository extends JpaRepository<ArchiveComment, Long> {
    
    // 특정 아카이브의 최상위 댓글만 페이징하여 조회 (대댓글 제외)
    @Query("SELECT c FROM ArchiveComment c " +
           "WHERE c.archive.id = :archiveId " +
           "AND c.parent IS NULL " +
           "ORDER BY c.createdDateTime DESC")
    Page<ArchiveComment> findParentCommentsByArchiveId(
        @Param("archiveId") Long archiveId, 
        Pageable pageable
    );

    // 특정 댓글의 대댓글 목록 조회
    @Query("SELECT c FROM ArchiveComment c " +
           "WHERE c.parent.id = :parentId " +
           "ORDER BY c.createdDateTime ASC")
    List<ArchiveComment> findRepliesByParentId(@Param("parentId") Long parentId);

    // 특정 아카이브의 특정 작성자가 작성한 댓글 찾기
    Optional<ArchiveComment> findByIdAndArchiveAndWriter(
        Long id, 
        Archive archive, 
        String writer
    );


    void deleteByArchiveId(Long archiveId);
} 