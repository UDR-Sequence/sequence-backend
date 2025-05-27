package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import sequence.sequence_member.member.entity.EmailAuthTokenEntity;

public interface EmailAuthTokenRepository extends JpaRepository<EmailAuthTokenEntity, Long> {
    Optional<EmailAuthTokenEntity> findByEmailAndToken(String email, String token);
}