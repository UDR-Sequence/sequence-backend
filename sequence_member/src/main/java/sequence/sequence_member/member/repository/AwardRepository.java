package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.entity.AwardEntity;


@Repository
public interface AwardRepository extends JpaRepository<AwardEntity,Long> {
    @Transactional
    @Modifying
    void deleteByMemberId(Long Id);
}
