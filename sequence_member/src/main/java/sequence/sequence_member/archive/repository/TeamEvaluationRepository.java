package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveMember;
import sequence.sequence_member.archive.entity.TeamEvaluation;

import java.util.List;

public interface TeamEvaluationRepository extends JpaRepository<TeamEvaluation, Long> {
    
    // ========== 소프트 삭제 적용된 메서드들 (JPA 네이밍 컨벤션) ==========
    
    // 평가자가 특정 피평가자를 평가했는지 확인 (삭제되지 않은 것만)
    boolean existsByEvaluatorAndEvaluatedAndIsDeletedFalse(ArchiveMember evaluator, ArchiveMember evaluated);
    
    // 평가자와 피평가자로 평가 정보 조회 (삭제되지 않은 것만)
    TeamEvaluation findByEvaluatorAndEvaluatedAndIsDeletedFalse(ArchiveMember evaluator, ArchiveMember evaluated);
    
    // 특정 멤버가 받은 평가 수 조회 (삭제되지 않은 것만)
    long countByEvaluatedAndIsDeletedFalse(ArchiveMember evaluated);

    // 특정 아카이브의 평가자 목록 조회 (삭제되지 않은 것만)
    @Query("SELECT DISTINCT t.evaluator FROM TeamEvaluation t WHERE t.evaluator.archive = :archive AND t.isDeleted = false")
    List<ArchiveMember> findDistinctEvaluatorsByArchiveAndNotDeleted(Archive archive);

    // 특정 평가자의 모든 평가 조회 (삭제되지 않은 것만)
    List<TeamEvaluation> findAllByEvaluatorAndEvaluator_Archive_IdAndIsDeletedFalse(ArchiveMember evaluator, Long archiveId);

    // 특정 멤버가 받은 평가 조회 (삭제되지 않은 것만)
    List<TeamEvaluation> findByEvaluated_Member_IdAndIsDeletedFalse(Long memberId);

    // 특정 아카이브의 모든 팀원 간 상호평가가 완료되었는지 확인 (삭제되지 않은 것만)
    @Query("SELECT COUNT(te) = " +
           "(SELECT COUNT(am1) * (COUNT(am2) - 1) FROM ArchiveMember am1, ArchiveMember am2 " +
           "WHERE am1.archive = :archive AND am2.archive = :archive AND am1 != am2) " +
           "FROM TeamEvaluation te " +
           "WHERE te.evaluator.archive = :archive AND te.isDeleted = false")
    boolean isAllEvaluationCompletedInArchiveAndNotDeleted(@Param("archive") Archive archive);
    
    // ========== 호환성을 위한 기존 메서드들 ==========
    
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

    @Query("SELECT te FROM TeamEvaluation te WHERE te.evaluated.member.id = :memberId")
    List<TeamEvaluation> findByEvaluatedId(@Param("memberId") Long memberId);
} 
