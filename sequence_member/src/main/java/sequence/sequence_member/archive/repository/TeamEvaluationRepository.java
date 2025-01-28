package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.TeamEvaluationEntity;

public interface TeamEvaluationRepository extends JpaRepository<TeamEvaluationEntity, Long> {
}
