package sequence.sequence_member.member.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
    Optional<MemberEntity> findByUsername(String username);

    List<MemberEntity> findByUsernameIn(List<String> usernameList);
}
