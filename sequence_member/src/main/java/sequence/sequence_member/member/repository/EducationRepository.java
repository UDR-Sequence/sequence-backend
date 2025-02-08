package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.EducationEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.Optional;

public interface EducationRepository extends JpaRepository<EducationEntity, Long> {
    Optional<EducationEntity> findByMember(MemberEntity member);
}
