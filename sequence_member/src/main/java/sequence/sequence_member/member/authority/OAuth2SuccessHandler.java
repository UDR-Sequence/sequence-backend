package sequence.sequence_member.member.authority;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import sequence.sequence_member.global.enums.enums.AuthProvider;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.member.service.TokenReissueService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final TokenReissueService tokenReissueService;
    private final JWTUtil jwtUtil;
    private final long ACCESS_TOKEN_EXPIRED_TIME = 600000L*60*1; // 1시간
    private final long REFRESH_TOKEN_EXPIRED_TIME = 600000L*60*24*7; // 7일

    public OAuth2SuccessHandler(
            MemberRepository memberRepository,
            TokenReissueService tokenReissueService,
            JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.tokenReissueService = tokenReissueService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();

        log.info("OAuth2 oidcUser={}", oidcUser);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String providerId = oidcUser.getName(); // 고유 사용자 ID

        Optional<MemberEntity> optionalUser = memberRepository.findByEmail(email);

        MemberEntity member = optionalUser.orElseGet(() -> {
            MemberEntity newUser = MemberEntity.createSocialMember(
                    email,
                    name,
                    AuthProvider.GOOGLE,
                    providerId
            );
            return memberRepository.save(newUser);
        });

        log.info("OAuth2 로그인 성공: email={}, provider={}", member.getEmail(), member.getProvider());

        String username = member.getUsername();

        String access = jwtUtil.createJwt("access", username, ACCESS_TOKEN_EXPIRED_TIME);
        String refresh = jwtUtil.createJwt("refresh", username, REFRESH_TOKEN_EXPIRED_TIME);

        tokenReissueService.RefreshTokenSave(username, refresh, REFRESH_TOKEN_EXPIRED_TIME);

        // 헤더에 accessToken 설정
        response.setHeader("access", access);

        // 쿠키에 refreshToken 설정
        Cookie refreshTokenCookie = new Cookie("refresh", refresh);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // 배포 시 true (https)
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (REFRESH_TOKEN_EXPIRED_TIME / 1000));
        response.addCookie(refreshTokenCookie);

        String redirectUri = UriComponentsBuilder
                .fromUriString("http://localhost:3000/oauth/callback")
                .queryParam("access", access)
                .build().toUriString();

        response.sendRedirect(redirectUri); // 리디렉션 수행
    }
}
