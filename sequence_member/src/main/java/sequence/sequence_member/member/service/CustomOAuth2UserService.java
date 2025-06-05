package sequence.sequence_member.member.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.enums.enums.AuthProvider;
import sequence.sequence_member.member.dto.MemberPrincipal;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.oauth.OAuth2UserInfo;
import sequence.sequence_member.member.oauth.OAuth2UserInfoFactory;
import sequence.sequence_member.member.repository.MemberRepository;

@Service
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 사용자 정보 파싱
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oauth2User.getAttributes());

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 provider에서 이메일을 찾을 수 없습니다.");
        }

        AuthProvider provider = getAuthProvider(registrationId);
        MemberEntity member = memberRepository.findByEmailAndProvider(oauth2UserInfo.getEmail(), provider)
                .map(existing -> updateExistingMember(existing, oauth2UserInfo))
                .orElseGet(() -> registerNewMember(userRequest, oauth2UserInfo));

        return MemberPrincipal.create(member, oauth2User.getAttributes());
    }

    private MemberEntity registerNewMember(OAuth2UserRequest userRequest, OAuth2UserInfo userInfo) {
        AuthProvider provider = getAuthProvider(userRequest.getClientRegistration().getRegistrationId());

        MemberEntity member = MemberEntity.createSocialMember(
                userInfo.getEmail(),
                userInfo.getName() != null ? userInfo.getName() : "사용자",
                provider,
                userInfo.getId()
        );

        return memberRepository.save(member);
    }

    private MemberEntity updateExistingMember(MemberEntity existing, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null) {
            existing.setName(userInfo.getName());
        }
        return memberRepository.save(existing);
    }

    private AuthProvider getAuthProvider(String registrationId) {
        switch (registrationId.toUpperCase()) {
            case "GOOGLE":
                return AuthProvider.GOOGLE;
//            case "KAKAO":
//                return AuthProvider.KAKAO;
            default:
                throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + registrationId);
        }
    }
}
