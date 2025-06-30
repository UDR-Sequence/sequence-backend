package sequence.sequence_member.archive.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.Status;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공

    // 기본 조회 - 삭제되지 않은 아카이브만
    Optional<Archive> findByIdAndIsDeletedFalse(Long id);
    
    // 작성자 정보를 함께 조회하는 최적화된 메서드 (FETCH JOIN 사용)
    @Query("SELECT a FROM Archive a JOIN FETCH a.writer WHERE a.id = :id AND a.isDeleted = false")
    Optional<Archive> findByIdAndIsDeletedFalseWithWriter(@Param("id") Long id);
    
    // 전체 목록 조회 - 삭제되지 않은 것만
    Page<Archive> findByIsDeletedFalse(Pageable pageable);
    
    // 상태별 조회 - 삭제되지 않은 것만
    Page<Archive> findByStatusAndIsDeletedFalse(Status status, Pageable pageable);
    
    // 상태별 조회 - 작성자 정보 포함 (FETCH JOIN 사용)
    @Query("SELECT a FROM Archive a JOIN FETCH a.writer WHERE a.status = :status AND a.isDeleted = false")
    Page<Archive> findByStatusAndIsDeletedFalseWithWriter(@Param("status") Status status, Pageable pageable);
    
    // 카테고리별 조회 - 삭제되지 않은 것만
    Page<Archive> findByCategoryAndIsDeletedFalse(Category category, Pageable pageable);
    
    // 카테고리와 상태로 조회 - 삭제되지 않은 것만
    Page<Archive> findByCategoryAndStatusAndIsDeletedFalse(Category category, Status status, Pageable pageable);
    
    // 카테고리와 상태로 조회 - 작성자 정보 포함 (FETCH JOIN 사용)
    @Query("SELECT a FROM Archive a JOIN FETCH a.writer WHERE a.category = :category AND a.status = :status AND a.isDeleted = false")
    Page<Archive> findByCategoryAndStatusAndIsDeletedFalseWithWriter(@Param("category") Category category, @Param("status") Status status, Pageable pageable);
    
    // 제목으로 검색 - 삭제되지 않은 것만
    Page<Archive> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title, Pageable pageable);
    
    // 제목으로 검색하고 상태로 필터링 - 삭제되지 않은 것만
    Page<Archive> findByTitleContainingIgnoreCaseAndStatusAndIsDeletedFalse(String title, Status status, Pageable pageable);
    
    // 제목으로 검색하고 상태로 필터링 - 작성자 정보 포함 (FETCH JOIN 사용)
    @Query("SELECT a FROM Archive a JOIN FETCH a.writer WHERE a.title LIKE %:title% AND a.status = :status AND a.isDeleted = false")
    Page<Archive> findByTitleContainingIgnoreCaseAndStatusAndIsDeletedFalseWithWriter(@Param("title") String title, @Param("status") Status status, Pageable pageable);
    
    // 카테고리와 제목으로 검색 - 삭제되지 않은 것만
    Page<Archive> findByCategoryAndTitleContainingIgnoreCaseAndIsDeletedFalse(Category category, String title, Pageable pageable);
    
    // 카테고리와 제목으로 검색하고 상태로 필터링 - 삭제되지 않은 것만
    Page<Archive> findByCategoryAndTitleContainingIgnoreCaseAndStatusAndIsDeletedFalse(Category category, String title, Status status, Pageable pageable);
    
    // 카테고리와 제목으로 검색하고 상태로 필터링 - 작성자 정보 포함 (FETCH JOIN 사용)
    @Query("SELECT a FROM Archive a JOIN FETCH a.writer WHERE a.category = :category AND a.title LIKE %:title% AND a.status = :status AND a.isDeleted = false")
    Page<Archive> findByCategoryAndTitleContainingIgnoreCaseAndStatusAndIsDeletedFalseWithWriter(@Param("category") Category category, @Param("title") String title, @Param("status") Status status, Pageable pageable);
    
    // 멤버 ID로 아카이브 검색 - 삭제되지 않은 것만
    @Query("SELECT a FROM Archive a JOIN a.archiveMembers am WHERE am.member.id = :memberId AND a.isDeleted = false")
    Page<Archive> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
    
    // 유저가 참여하고 있는 아카이브 조회 (최신순 10개) - 삭제되지 않은 것만
    @Query("SELECT a FROM Archive a JOIN a.archiveMembers am WHERE am.member.id = :memberId AND a.isDeleted = false ORDER BY a.createdDateTime DESC")
    List<Archive> findTop10ByMemberId(@Param("memberId") Long memberId);
    
    // 특정 멤버의 아카이브 중 상태가 평가완료인 것만 조회 (최신순 5개) - 삭제되지 않은 것만
    @Query("SELECT a FROM Archive a JOIN a.archiveMembers am WHERE am.member.id = :memberId AND a.status = :status AND a.isDeleted = false ORDER BY a.createdDateTime DESC")
    List<Archive> findTop5ByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") Status status);
    
    // 특정 멤버가 작성한 모든 아카이브 조회 - 삭제되지 않은 것만
    List<Archive> findByWriterAndIsDeletedFalse(MemberEntity writer, Sort sort);
    Page<Archive> findByWriterAndIsDeletedFalse(MemberEntity writer, Pageable pageable);
    
    // 조회수 조회 - 삭제되지 않은 것만
    @Query("SELECT a.view FROM Archive a WHERE a.id = :archiveId AND a.isDeleted = false")
    Optional<Integer> findViewById(@Param("archiveId") Long archiveId);
    
    // 아카이브 존재 확인 - 삭제되지 않은 것만
    boolean existsByIdAndIsDeletedFalse(Long id);

    // ========== 호환성을 위한 기존 메서드들 (모든 아카이브 포함) ==========
    // 실제 사용을 권장하지 않음 - 삭제된 아카이브도 포함됨
    
    Optional<Archive> findById(Long id);
    
    Page<Archive> findByCategoryAndTitleContainingIgnoreCase(Category category, String title, Pageable pageable);
    Page<Archive> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Archive> findByArchiveMembers_Member_Id(Long memberId, Pageable pageable);
    List<Archive> findTop10ByArchiveMembers_Member_IdOrderByCreatedDateTimeDesc(Long archiveMembersMemberId);
    List<Archive> findTop5ByArchiveMembers_Member_IdAndStatusOrderByCreatedDateTimeDesc(Long memberId, Status status);

} 
