package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.ArchiveEntity;

public interface ArchiveRepository extends JpaRepository<ArchiveEntity, Long> {
}
