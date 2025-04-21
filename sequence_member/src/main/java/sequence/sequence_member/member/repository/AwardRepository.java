package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;


@Repository
public interface AwardRepository extends JpaRepository<AwardEntity,Long> {
    @Query("SELECT a FROM AwardEntity a WHERE a.member = :member AND a.isDeleted = false")
    List<AwardEntity> findByMemberAndIsDeletedFalse(MemberEntity member);

    @Transactional
    @Modifying
    void deleteByMemberId(Long Id);
}
