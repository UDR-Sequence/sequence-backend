package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;

public interface CareerRepository extends JpaRepository<CareerEntity, Long> {

    List<CareerEntity> findByMember(MemberEntity member);

    @Transactional
    @Modifying
    void deleteByMemberId(Long Id);
}
