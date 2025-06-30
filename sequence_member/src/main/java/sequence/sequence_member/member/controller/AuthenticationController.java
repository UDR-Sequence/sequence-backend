package sequence.sequence_member.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import sequence.sequence_member.global.response.ApiResponseData;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/social")
public class AuthenticationController {
    @GetMapping("/bind/google")
    public ResponseEntity<ApiResponseData<String>> bindGoogle(
            HttpSession session
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("계정 연동 요청: username = {}, provider = google", username);

        String bindingDataSessionKey = "oauth2_binding_username";
        session.setAttribute(bindingDataSessionKey, username);
        log.info("세션에 바인딩할 사용자 이름 저장 완료: {}", username);

        String bindingStateToken = "bind:" + UUID.randomUUID();
        String redirectUri = UriComponentsBuilder
                .fromUriString("http://localhost:8080" + "/oauth2/authorization/google")
                .queryParam("binding_state_token", bindingStateToken)
                .build()
                .toUriString();

        return ResponseEntity.ok().body(ApiResponseData.success(redirectUri));
    }
}
