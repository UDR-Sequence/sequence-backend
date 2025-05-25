package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import java.util.Optional;

public interface FindPasswordRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUsername(String username, String email);
    boolean existsByUsername(String username);
}
