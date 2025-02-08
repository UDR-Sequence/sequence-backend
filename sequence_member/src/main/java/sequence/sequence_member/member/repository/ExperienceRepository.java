package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<ExperienceEntity, Long> {
    List<ExperienceEntity> findByMember(MemberEntity member);
}
