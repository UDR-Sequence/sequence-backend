package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.CareerEntity;

public interface CareerRepository extends JpaRepository<CareerEntity, Long> {
    @Transactional
    @Modifying
    void deleteByMemberId(Long Id);
}
