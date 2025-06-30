package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.AuthProvider;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_auth_provider")
public class MemberAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private AuthProvider provider;

    @Column(name = "provider_id", nullable = true, unique = true, length = 255)
    private String providerId;

    public MemberAuthProvider(MemberEntity member, AuthProvider provider, String providerId) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
    }

    protected void setMember(MemberEntity member) {
        this.member = member;
    }
}
