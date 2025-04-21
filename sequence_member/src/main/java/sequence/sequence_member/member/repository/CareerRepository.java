package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;

public interface CareerRepository extends JpaRepository<CareerEntity, Long> {
    @Query("SELECT c FROM CareerEntity c WHERE c.member = :member AND c.isDeleted = false")
    List<CareerEntity> findByMemberAndIsDeletedFalse(MemberEntity member);

    @Transactional
    @Modifying
    void deleteByMemberId(Long Id);
}
