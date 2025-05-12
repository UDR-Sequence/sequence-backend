package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.entity.PasswordResetEntity;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetEntity, String> {
    Optional<PasswordResetEntity> findByToken(String token);
    void deleteByToken(String token);

    Optional<PasswordResetEntity> findByMember(MemberEntity member);
}
