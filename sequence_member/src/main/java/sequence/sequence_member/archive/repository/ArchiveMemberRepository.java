package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.ArchiveMemberEntity;

public interface ArchiveMemberRepository extends JpaRepository<ArchiveMemberEntity, Long> {
}
