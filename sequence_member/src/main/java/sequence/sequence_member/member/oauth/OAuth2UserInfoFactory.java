package sequence.sequence_member.member.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
//            case "kakao":
//                return new KakaoOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + registrationId);
        }
    }
}
