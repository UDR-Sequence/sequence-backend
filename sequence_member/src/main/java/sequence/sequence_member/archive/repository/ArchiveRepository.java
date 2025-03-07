package sequence.sequence_member.archive.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.global.enums.enums.Category;
import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공

    //아카이브 등록 후 Read
    Optional<Archive> findById(Long id);
    
    // 카테고리와 제목으로 검색
    Page<Archive> findByCategoryAndTitleContainingIgnoreCase(Category category, String title, Pageable pageable);
    
    // 카테고리로만 검색
    Page<Archive> findByCategory(Category category, Pageable pageable);
    
    // 제목으로만 검색
    Page<Archive> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    
    // 전체 목록 조회는 JpaRepository의 findAll(Pageable) 사용
} 