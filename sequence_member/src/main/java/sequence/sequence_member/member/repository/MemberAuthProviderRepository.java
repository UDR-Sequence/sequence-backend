package sequence.sequence_member.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sequence.sequence_member.global.enums.enums.AuthProvider;
import sequence.sequence_member.member.entity.MemberAuthProvider;

import java.util.Optional;

@Repository
public interface MemberAuthProviderRepository extends JpaRepository<MemberAuthProvider, Long> {

    /**
     * 특정 회원 ID와 제공자(Provider)로 MemberAuthProvider를 조회합니다.
     * 계정 연동 시 해당 회원이 이미 해당 제공자로 연동되어 있는지 확인할 때 사용합니다.
     *
     * @param memberId 조회할 회원 ID
     * @param provider 소셜 로그인 제공자 타입 (예: AuthProvider.GOOGLE)
     * @return MemberAuthProvider 객체 (존재하지 않으면 Optional.empty())
     */
    Optional<MemberAuthProvider> findByMemberIdAndProvider(Long memberId, AuthProvider provider);

    /**
     * 특정 제공자(Provider)와 해당 제공자의 사용자 고유 ID(providerId)로 MemberAuthProvider를 조회합니다.
     * 소셜 로그인 시 기존 가입자를 찾거나, 새로운 연동을 시도할 때 해당 소셜 계정이 이미 다른 회원에게 연동되어 있는지 확인할 때 사용합니다.
     *
     * @param provider 소셜 로그인 제공자 타입 (예: AuthProvider.GOOGLE)
     * @param providerId 소셜 로그인 제공자의 사용자 고유 ID
     * @return MemberAuthProvider 객체 (존재하지 않으면 Optional.empty())
     */
    Optional<MemberAuthProvider> findByProviderAndProviderId(AuthProvider provider, String providerId);
}