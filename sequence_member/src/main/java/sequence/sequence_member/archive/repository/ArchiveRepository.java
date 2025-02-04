package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.ArchiveEntity;
import java.util.List;

public interface ArchiveRepository extends JpaRepository<ArchiveEntity, Long> {
    // 특정 상태(status)의 아카이브 조회
    List<ArchiveEntity> findByStatus(Byte status);

    // 제목 검색 기능 추가
    List<ArchiveEntity> findByTitleContaining(String keyword);
}
