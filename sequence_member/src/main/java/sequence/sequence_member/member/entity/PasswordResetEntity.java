package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetEntity {
    @Id
    @Column(name = "token", length = 36, nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private MemberEntity member;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public PasswordResetEntity(MemberEntity member, Duration validDuration) {
        this.token = UUID.randomUUID().toString();
        this.member = member;
        this.expiryDate = LocalDateTime.now().plus(validDuration);
    }

    /* 토큰이 만료되었는지 여부를 반환 */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

}
