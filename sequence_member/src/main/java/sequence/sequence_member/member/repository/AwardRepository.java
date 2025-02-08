package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;

public interface AwardRepository extends JpaRepository<AwardEntity,Long> {
    List<AwardEntity> findByMember(MemberEntity member);
}
