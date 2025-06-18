package sequence.sequence_member.member.authority;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import sequence.sequence_member.member.dto.MemberPrincipal;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.service.TokenReissueService;

import java.io.IOException;

@Slf4j // Lombok 어노테이션
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenReissueService tokenReissueService;
    private final JWTUtil jwtUtil;
    private final long ACCESS_TOKEN_EXPIRED_TIME = 600000L * 60 * 1; // 1시간
    private final long REFRESH_TOKEN_EXPIRED_TIME = 600000L * 60 * 24 * 7; // 7일

    public OAuth2SuccessHandler(
            TokenReissueService tokenReissueService,
            JWTUtil jwtUtil) {
        this.tokenReissueService = tokenReissueService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        MemberPrincipal memberPrincipal = (MemberPrincipal) authentication.getPrincipal();

        log.info("OAuth2 인증 성공, Principal: {}", memberPrincipal);

        MemberEntity member = memberPrincipal.getMemberEntity();

        String email = member.getEmail();
        if (email == null || email.isEmpty()) {
            log.error("인증된 Principal에서 이메일(email)이 null 또는 비어있습니다. 로그인 처리 실패.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found in OAuth2 principal.");
            return;
        }

        log.info("OAuth2 로그인 성공: email={}", email);

        String username = member.getUsername();

        String access = jwtUtil.createJwt("access", username, ACCESS_TOKEN_EXPIRED_TIME);
        String refresh = jwtUtil.createJwt("refresh", username, REFRESH_TOKEN_EXPIRED_TIME);

        tokenReissueService.RefreshTokenSave(username, refresh, REFRESH_TOKEN_EXPIRED_TIME);

        // 헤더에 accessToken 설정
        response.setHeader("access", access);

        // 쿠키에 refreshToken 설정
        Cookie refreshTokenCookie = new Cookie("refresh", refresh);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // 로컬 HTTP 환경에서는 false, 배포 HTTPS 시 true로 변경해야 합니다.
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
