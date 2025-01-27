package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.AwardEntity;

public interface AwardRepository extends JpaRepository<AwardEntity,Long> {
}
