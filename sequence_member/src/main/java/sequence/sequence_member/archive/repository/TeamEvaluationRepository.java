package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveMember;
import sequence.sequence_member.archive.entity.TeamEvaluation;

import java.util.List;

public interface TeamEvaluationRepository extends JpaRepository<TeamEvaluation, Long> {
    
    // 평가자가 특정 피평가자를 평가했는지 확인
    boolean existsByEvaluatorAndEvaluated(ArchiveMember evaluator, ArchiveMember evaluated);
    
    // 특정 아카이브의 모든 팀원 간 상호평가가 완료되었는지 확인
    @Query("SELECT COUNT(te) = " +
           "(SELECT COUNT(am1) * (COUNT(am2) - 1) FROM ArchiveMember am1, ArchiveMember am2 " +
           "WHERE am1.archive = :archive AND am2.archive = :archive AND am1 != am2) " +
           "FROM TeamEvaluation te " +
           "WHERE te.evaluator.archive = :archive")
    boolean isAllEvaluationCompletedInArchive(@Param("archive") Archive archive);
    
    // 평가자와 피평가자로 평가 정보 조회
    TeamEvaluation findByEvaluatorAndEvaluated(ArchiveMember evaluator, ArchiveMember evaluated);
    
    // 특정 멤버가 받은 평가 수 조회
    long countByEvaluated(ArchiveMember evaluated);

    @Query("SELECT DISTINCT t.evaluator FROM TeamEvaluation t WHERE t.evaluator.archive = :archive")
    List<ArchiveMember> findDistinctEvaluatorsByArchive(Archive archive);

    List<TeamEvaluation> findAllByEvaluatorAndEvaluator_Archive_Id(ArchiveMember evaluator, Long archiveId);
} 
