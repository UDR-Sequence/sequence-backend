package sequence.sequence_member.archive.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.ArchiveComment;
import sequence.sequence_member.archive.entity.Archive;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchiveCommentRepository extends JpaRepository<ArchiveComment, Long> {
    
    // ========== 소프트 삭제 적용된 메서드들 (JPA 네이밍 컨벤션) ==========
    
    // 삭제되지 않은 댓글만 조회
    Optional<ArchiveComment> findByIdAndIsDeletedFalse(Long id);
    
    // 특정 아카이브의 최상위 댓글만 페이징하여 조회 (삭제되지 않은 것만)
    Page<ArchiveComment> findByArchiveIdAndParentIsNullAndIsDeletedFalseOrderByCreatedDateTimeAsc(
        Long archiveId, 
        Pageable pageable
    );

    // 특정 댓글의 대댓글 목록 조회 (삭제되지 않은 것만)
    List<ArchiveComment> findByParentIdAndIsDeletedFalseOrderByCreatedDateTimeAsc(Long parentId);

    // 특정 아카이브의 특정 작성자가 작성한 댓글 찾기 (삭제되지 않은 것만)
    Optional<ArchiveComment> findByIdAndArchiveAndWriterAndIsDeletedFalse(
        Long id, 
        Archive archive, 
        String writer
    );

    // ========== 호환성을 위한 기존 메서드들 ==========
    
    // 특정 아카이브의 최상위 댓글만 페이징하여 조회 (대댓글 제외, 오래된 순)
    Page<ArchiveComment> findByArchiveIdAndParentIsNullOrderByCreatedDateTimeAsc(
        Long archiveId, 
        Pageable pageable
    );

    // 특정 댓글의 대댓글 목록 조회 (오래된 순)
    List<ArchiveComment> findByParentIdOrderByCreatedDateTimeAsc(Long parentId);

    // 특정 아카이브의 특정 작성자가 작성한 댓글 찾기
    Optional<ArchiveComment> findByIdAndArchiveAndWriter(
        Long id, 
        Archive archive, 
        String writer
    );

} 