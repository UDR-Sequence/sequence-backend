package sequence.sequence_member.member.authority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2FailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {

        logger.error("소셜 로그인 실패: {}", exception.getMessage());
        exception.printStackTrace(); // 에러 로그 확인
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"소셜 로그인에 실패했습니다: " + exception.getMessage() + "\"}");
    }
}
