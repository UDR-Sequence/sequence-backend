package sequence.sequence_member.member.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthorizationRequestRepository.class);

     public static final String SESSION_ATTR_NAME = "SPRING_SECURITY_OAUTH2_AUTHORIZATION_REQUEST";
     public static final String SPRING_SECURITY_OAUTH2_BINDING_DATA = "SPRING_SECURITY_OAUTH2_BINDING_DATA";

     private final HttpSessionOAuth2AuthorizationRequestRepository delegate = new HttpSessionOAuth2AuthorizationRequestRepository();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        logger.debug("DEBUG: loadAuthorizationRequest - Session ID: {}", session != null ? session.getId() : "null");
        if (session != null) {
            StringBuilder attributes = new StringBuilder("[");
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                attributes.append(attributeNames.nextElement());
                if (attributeNames.hasMoreElements()) {
                    attributes.append(", ");
                }
            }
            attributes.append("]");
            logger.debug("DEBUG: loadAuthorizationRequest - Session attributes: {}", attributes.toString());
        }
        return delegate.loadAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logger.info("🔐 saveAuthorizationRequest 호출됨 - URI: {} (Method: {})", request.getRequestURI(), request.getMethod()); // ⭐ 요청 URI 및 메서드 로그 추가

        HttpSession session = request.getSession();
        logger.debug("Session ID (save): {}", session.getId());
        logger.debug("Session attributes BEFORE delegate save: {}", getSessionAttributesString(session));

        if (authorizationRequest == null) {
            delegate.saveAuthorizationRequest(null, request, response);
            logger.debug("Session attributes after null request cleanup: {}", getSessionAttributesString(session));
            return;
        }

        // Spring Security가 생성한 기본 state 값 (CSRF 방어용)
        String originalSpringSecurityState = authorizationRequest.getState();
        // 클라이언트(프런트엔드)로부터 전달된 커스텀 파라미터 (계정 연동 요청 시 전달됨)
        String bindingUserIdStr = request.getParameter("binding_user_id");
        String bindingStateToken = request.getParameter("binding_state_token");

        logger.debug("DEBUG: Original SS State = {}", originalSpringSecurityState);
        logger.debug("DEBUG: binding_user_id Parameter = {}", bindingUserIdStr);
        logger.debug("DEBUG: binding_state_token Parameter = {}", bindingStateToken);

        // 1. Spring Security의 OAuth2AuthorizationRequest 객체는 delegate를 통해 세션에 저장
        delegate.saveAuthorizationRequest(authorizationRequest, request, response);
        logger.debug("DEBUG: After setting {}: {}", SESSION_ATTR_NAME, getSessionAttributesString(session));

        // 2. 만약 커스텀 연동 파라미터(userId와 bindingStateToken)가 존재하고 유효하면,
        //    이 정보를 Map 형태로 구성하여 세션에 별도로 저장
        if (bindingUserIdStr != null && bindingStateToken != null && bindingStateToken.startsWith("bind:")) {
            // Map의 키를 originalSpringSecurityState와 조합하여 세션에 저장
            String bindingDataKey = SPRING_SECURITY_OAUTH2_BINDING_DATA + "_" + originalSpringSecurityState;

            Long userId = null;
            try {
                userId = Long.parseLong(bindingUserIdStr);
            } catch (NumberFormatException e) {
                logger.warn("WARN: 'binding_user_id' 파라미터({})를 Long으로 파싱할 수 없습니다.", bindingUserIdStr);
            }

            Map<String, Object> bindingMap = new HashMap<>();
            bindingMap.put("bindState", bindingStateToken);
            if (userId != null) {
                bindingMap.put("userId", userId);
            } else {
                logger.warn("WARN: bindingMap에 userId가 null로 저장됩니다. 파라미터 값: {}", bindingUserIdStr);
            }

            session.setAttribute(bindingDataKey, bindingMap);
            logger.info("📦 세션에 연동 데이터 저장 완료 (키: {}, 값: {})", bindingDataKey, bindingMap);
            logger.debug("Session attributes after save (detailed): {}", getSessionAttributesString(session));
        } else {
            logger.debug("📦 커스텀 연동 파라미터가 없거나 유효하지 않습니다. (bindingUserIdStr: {}, bindingStateToken: {})", bindingUserIdStr, bindingStateToken);
            logger.debug("Session attributes after save (no binding data stored): {}", getSessionAttributesString(session));
        }

        logger.info("🚀 IdP로 리다이렉트될 때 사용될 state는 '{}'입니다. (이 값이 Google로 보내집니다)", originalSpringSecurityState);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);
        logger.debug("DEBUG: removeAuthorizationRequest - Session ID: {}", session != null ? session.getId() : "null");
        logger.debug("DEBUG: removeAuthorizationRequest - Session attributes before removal: {}", getSessionAttributesString(session));

        OAuth2AuthorizationRequest authorizationRequest = delegate.removeAuthorizationRequest(request, response);

        logger.debug("DEBUG: removeAuthorizationRequest - Session attributes after removal: {}", getSessionAttributesString(session));

        return authorizationRequest;
    }

    private String getSessionAttributesString(HttpSession session) {
        if (session == null) {
            return "[]";
        }
        StringBuilder attributes = new StringBuilder("[");
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            attributes.append(attributeNames.nextElement());
            if (attributeNames.hasMoreElements()) {
                attributes.append(", ");
            }
        }
        attributes.append("]");
        return attributes.toString();
    }
}
