package sequence.sequence_member.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sequence.sequence_member.global.enums.enums.AuthProvider;
import sequence.sequence_member.member.dto.MemberPrincipal;
import sequence.sequence_member.member.entity.MemberAuthProvider;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.oauth.OAuth2UserInfo;
import sequence.sequence_member.member.oauth.OAuth2UserInfoFactory;
import sequence.sequence_member.member.repository.MemberAuthProviderRepository;
import sequence.sequence_member.member.repository.MemberRepository;

import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;

import static sequence.sequence_member.member.repository.CustomAuthorizationRequestRepository.SPRING_SECURITY_OAUTH2_BINDING_DATA;

@Service
@Transactional
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final MemberRepository memberRepository;
    private final MemberAuthProviderRepository memberAuthProviderRepository; // 의존성 주입

    public CustomOidcUserService(MemberRepository memberRepository, MemberAuthProviderRepository memberAuthProviderRepository) {
        this.memberRepository = memberRepository;
        this.memberAuthProviderRepository = memberAuthProviderRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("✅ CustomOidcUserService loadUser 호출됨");
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        logger.debug("DEBUG: OidcUser Attributes: {}", oidcUser.getAttributes());

        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oidcUser.getAttributes());

        logger.debug("DEBUG: OAuth2UserInfo Extracted Email: {}", oauth2UserInfo.getEmail());

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            logger.error("ERROR: OAuth2UserInfo에서 이메일이 null이거나 비어있습니다. 예외 발생.");
            throw new OAuth2AuthenticationException("OAuth2 provider에서 이메일을 찾을 수 없습니다.");
        }

        AuthProvider provider = getAuthProvider(registrationId); // registrationId로 AuthProvider 가져옴

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest not available in RequestContextHolder.");
        }

        logger.info("Session ID (load): {}", request.getSession().getId());
        StringBuilder sessionAttributes = new StringBuilder("[");
        Enumeration<String> attributeNames = request.getSession().getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            sessionAttributes.append(attributeNames.nextElement());
            if (attributeNames.hasMoreElements()) {
                sessionAttributes.append(", ");
            }
        }
        sessionAttributes.append("]");
        logger.debug("Session attributes before extract: {}", sessionAttributes);

        String returnedSpringSecurityState = request.getParameter("state");
        logger.info("반환된 state (IdP로부터): {}", returnedSpringSecurityState);

        String BIND_STATE_PREFIX = "bind:";

        boolean isBindingRequest = false;
        Map<String, Object> bindingData = null;

        if (returnedSpringSecurityState != null) {
            // CustomAuthorizationRequestRepository에서 저장한 바인딩 데이터를 세션에서 조회
            bindingData = (Map<String, Object>) request.getSession().getAttribute(
                    SPRING_SECURITY_OAUTH2_BINDING_DATA + "_" + returnedSpringSecurityState
            );

            if (bindingData != null) {
                String bindStateFromMap = (String) bindingData.get("bindState");
                String usernameFromMap = (String) bindingData.get("username");

                if (bindStateFromMap != null && bindStateFromMap.startsWith(BIND_STATE_PREFIX) && usernameFromMap != null) {
                    isBindingRequest = true;
                }
                // 세션에서 사용한 바인딩 데이터 제거 (한번 사용하면 제거)
                request.getSession().removeAttribute(
                        SPRING_SECURITY_OAUTH2_BINDING_DATA + "_" + returnedSpringSecurityState
                );
                logger.debug("DEBUG: Removed binding data map from session in CustomOidcUserService.");
            }
        }

        logger.info("추출된 연동용 데이터: {}", bindingData);
        logger.info("연동 요청 여부: {}", isBindingRequest);

        MemberEntity member;
        if (isBindingRequest && bindingData != null) {
            member = bindSocialAccount(bindingData, oauth2UserInfo, provider);
        } else {
            member = loginOrRegister(oauth2UserInfo, provider, userRequest);
        }

        logger.debug("DEBUG: Member created/retrieved Email (before MemberPrincipal.create): {}", member.getEmail());

        return MemberPrincipal.create(member, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    /**
     * 기존 회원에게 소셜 계정을 연동하는 로직
     * @param bindingData 세션에서 추출된 연동 관련 데이터 (username, bindState)
     * @param userInfo 소셜 제공자로부터 받은 사용자 정보
     * @param provider 소셜 제공자 타입
     * @return 연동 완료된 MemberEntity
     * @throws OAuth2AuthenticationException 계정 연동 실패 시 발생
     */
    private MemberEntity bindSocialAccount(
            Map<String, Object> bindingData,
            OAuth2UserInfo userInfo,
            AuthProvider provider
    ) throws OAuth2AuthenticationException {
        String bindState = (String) bindingData.get("bindState");
        String username = (String) bindingData.get("username");

        if (username == null || bindState == null) {
            logger.warn("WARN: 계정 연동을 위한 데이터가 불완전합니다. (username: {}, bindState: {})", username, bindState);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("INVALID_BINDING_REQUEST"),
                    "계정 연동을 위한 데이터가 불완전합니다. 다시 시도해주세요."
            );
        }
        logger.debug("DEBUG: bindSocialAccount에서 가져온 username: {} (bindState: {})", username, bindState);

        Optional<MemberEntity> memberOptional = memberRepository.findByUsernameAndIsDeletedFalse(username);
        MemberEntity member = memberOptional.orElse(null);

        if (member == null) {
            logger.warn("WARN: 연동을 시도한 사용자를 찾을 수 없습니다. username: {}", username);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("USER_NOT_FOUND"),
                    "연동하려는 계정을 찾을 수 없습니다."
            );
        }

        // 이미 연동된 구글 계정 유무 확인
        Optional<MemberAuthProvider> existingSocialLinkToAnyMember = memberAuthProviderRepository.findByProviderAndProviderId(provider, userInfo.getId());
        if (existingSocialLinkToAnyMember.isPresent()) {
            if (!existingSocialLinkToAnyMember.get().getMember().getId().equals(member.getId())) {
                logger.warn("WARN: 소셜 계정({}, {})이 이미 다른 회원({})에게 연동되어 있습니다.", provider, userInfo.getId(), existingSocialLinkToAnyMember.get().getMember().getEmail());
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("SOCIAL_ACCOUNT_ALREADY_LINKED"),
                        "이미 다른 계정에 연동된 소셜 계정입니다. 해당 계정으로 로그인해주세요."
                );
            } else {
                logger.info("INFO: 소셜 계정({}, {})이 이미 현재 로그인된 회원({})에게 연동되어 있습니다. 재로그인 처리.", provider, userInfo.getId(), member.getEmail());
                return member;
            }
        }

        // 새로운 MemberAuthProvider 생성 및 Member에 추가
        MemberAuthProvider authProvider = new MemberAuthProvider(
                member,
                provider,
                userInfo.getId()
        );
        member.addAuthProviderIfNotExists(authProvider);
        memberRepository.save(member);

        logger.info("🔗 계정 연동 완료: username = {}, Provider: {}", username, provider);
        return member;
    }

    /**
     * 소셜 로그인 또는 신규 회원 등록 로직
     * @param userInfo 소셜 제공자로부터 받은 사용자 정보
     * @param provider 소셜 제공자 타입
     * @param userRequest OIDC 사용자 요청 객체
     * @return 로그인 또는 등록된 MemberEntity
     */
    private MemberEntity loginOrRegister(
            OAuth2UserInfo userInfo,
            AuthProvider provider,
            OidcUserRequest userRequest
    ) {
        Optional<MemberAuthProvider> existingAuthProviderOptional = memberAuthProviderRepository.findByProviderAndProviderId(provider, userInfo.getId());

        MemberEntity member;
        if (existingAuthProviderOptional.isPresent()) {
            member = existingAuthProviderOptional.get().getMember();
            logger.info("[소셜 계정 로그인] 기존 사용자 로그인: email={}, Provider: {}", member.getEmail(), provider);
        } else {
            member = registerNewMember(userRequest, userInfo);
            logger.info("[소셜 계정 로그인] 새로운 사용자 등록: email={}, Provider: {}", member.getEmail(), provider);
        }

        return member;
    }

    /**
     * 새로운 소셜 로그인 회원을 등록하는 로직
     * @param userRequest OIDC 사용자 요청 객체
     * @param oAuth2UserInfo OAuth2UserInfo (표준화된 사용자 정보)
     * @return 새로 등록된 MemberEntity
     */
    private MemberEntity registerNewMember(OidcUserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        AuthProvider provider = getAuthProvider(userRequest.getClientRegistration().getRegistrationId());

        MemberEntity member = MemberEntity.createSocialMember(
                oAuth2UserInfo.getEmail(), // 이메일은 OAuth2UserInfoFactory에서 검증됨
                oAuth2UserInfo.getName() != null ? oAuth2UserInfo.getName() : "사용자"
        );

        MemberAuthProvider memberAuthProvider = new MemberAuthProvider(
                member,
                provider,
                oAuth2UserInfo.getId()
        );
        member.addAuthProviderIfNotExists(memberAuthProvider);

        logger.info("새로운 소셜 로그인 사용자 등록 완료: email={}, Provider: {}", member.getEmail(), provider);
        return memberRepository.save(member);
    }

    /**
     * registrationId (clientRegistration.registrationId)로부터 AuthProvider Enum 값을 가져옵니다.
     * @param registrationId 클라이언트 등록 ID (예: "google", "kakao")
     * @return 해당 AuthProvider Enum 값
     * @throws OAuth2AuthenticationException 지원하지 않는 제공자일 경우 발생
     */
    private AuthProvider getAuthProvider(String registrationId) throws OAuth2AuthenticationException {
        switch (registrationId.toUpperCase()) {
            case "GOOGLE":
                return AuthProvider.GOOGLE;
            // case "KAKAO":
            //     return AuthProvider.KAKAO;
            default:
                logger.warn("WARN: 지원하지 않는 소셜 로그인 서비스입니다: {}", registrationId);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("UNSUPPORTED_SOCIAL_PROVIDER"),
                        "지원하지 않는 소셜 로그인 서비스입니다: " + registrationId
                );
        }
    }
}