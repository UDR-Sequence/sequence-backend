package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<ExperienceEntity, Long> {
    @Query("SELECT e FROM ExperienceEntity e WHERE e.member = :member AND e.isDeleted = false")
    List<ExperienceEntity> findByMemberAndIsDeletedFalse(MemberEntity member);

    @Transactional
    @Modifying
    void deleteByMemberId(Long Id);
}
