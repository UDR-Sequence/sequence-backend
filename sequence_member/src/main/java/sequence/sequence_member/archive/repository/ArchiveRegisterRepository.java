package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.archive.entity.Archive;

@Repository
public interface ArchiveRegisterRepository extends JpaRepository<Archive, Long> {
}
