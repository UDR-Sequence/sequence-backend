package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.archive.entity.TeamEvaluationEntity;
import java.util.List;

public interface TeamEvaluationRepository extends JpaRepository<TeamEvaluationEntity, Long> {
    // 특정 평가자가 작성한 평가 목록 조회
    List<TeamEvaluationEntity> findByEvaluatorId_MemberId(Long evaluatorId);

    // 특정 피평가자에 대한 평가 목록 조회
    List<TeamEvaluationEntity> findByEvaluateeId_MemberId(Long evaluateeId);

    // 특정 아카이브 내에서 모든 평가가 완료되었는지 확인 (평가 개수 체크)
    boolean existsByEvaluateeId_MemberIdAndEvaluatorId_MemberId(Long evaluateeId, Long evaluatorId);
}
