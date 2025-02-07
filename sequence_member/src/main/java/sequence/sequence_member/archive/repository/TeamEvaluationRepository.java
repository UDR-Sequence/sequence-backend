package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.TeamEvaluationEntity;
import java.util.List;

public interface TeamEvaluationRepository extends JpaRepository<TeamEvaluationEntity, Long> {
    // 특정 평가자가 작성한 평가 목록 조회
    List<TeamEvaluationEntity> findByEvaluator_Id(Long evaluatorId);

    // 특정 피평가자에 대한 평가 목록 조회
    List<TeamEvaluationEntity> findByEvaluatee_Id(Long evaluateeId);
}
