package sequence.sequence_member.member.jwt;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sequence.sequence_member.global.exception.BaseException;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.LoginOutputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.member.service.TokenReissueService;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenReissueService tokenReissueService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    private final MemberRepository memberRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, TokenReissueService tokenReissueService, MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenReissueService = tokenReissueService;
        this.memberRepository=memberRepository;
        //spring security는 대부분의 로직이 필터 단에서 동작하게 된다. 로그인 또한, 필터에서 처리되고, (자동으로 엔드포인트는 "/login" 이 된다.)
        //UsernamePasswordAuthenticationFilter에서 매핑되어 처리된다. 이 필터를 상속받아 LoginFilter를 만들게 된다.
        //security에서 설정해주는 기본 url("/login")을 /api/login으로 변경
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청으로 부터 username, password를 추출한다.
//        String username = obtainUsername(request);
//        String password = obtainPassword(request);
        String username = null;
        String password = null;

        // JSON 형식인지 확인
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                // JSON 데이터를 읽어서 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);

                username = requestMap.get("username");
                password = requestMap.get("password");
            } catch (IOException e) {
                throw new AuthenticationServiceException("Failed to parse JSON request", e);
            }
        } else {
            // 기본 form-urlencoded 방식 처리
            username = obtainUsername(request);
            password = obtainPassword(request);
        }

        if (username == null || password == null) {
            throw new AuthenticationServiceException("Username or Password is missing");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);

    }

    //로그인 성공시 실행하는 메서드 (여기서 jwt를 발급하면 된다)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException{
        //유저 이름 찾기
        String username = authentication.getName();
        MemberEntity memberEntity = memberRepository.findByUsername(username).orElseThrow(()->new BaseException("해당 유저가 존재하지 않습니다."));
        
        String access = jwtUtil.createJwt("access",username,  600000L*60*24*100); // 24시간 *100 = 100일. 테스트를 위해 기한 늘림
        String refresh = jwtUtil.createJwt("refresh", username, 86400000L*100); // 24시간 *100 = 100일

        tokenReissueService.RefreshTokenSave(username,refresh,86400000L*100);

        // 실패 응답 객체 생성
        ResponseEntity<ApiResponseData<LoginOutputDTO>> responseBody = ResponseEntity.ok().body(ApiResponseData.success(new LoginOutputDTO(memberEntity.getNickname(), memberEntity.getProfileImg()), "로그인을 성공하였습니다."));

        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 변환 후 출력
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody.getBody()));

    }

    //로그인 실패시 실행하는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException failed) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 실패 응답 객체 생성
        ResponseEntity<ApiResponseData<String>> responseBody = ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "아이디 혹은 비밀번호를 다시 입력해주세요"));

        // JSON 변환 후 출력
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody.getBody()));
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true); // https일 경우 설정
//        cookie.setPath("/"); // 쿠키의 적용 범위 설정

        //js로 쿠키에 접근 못하게 함
        cookie.setHttpOnly(true);

        return cookie;
    }
}
