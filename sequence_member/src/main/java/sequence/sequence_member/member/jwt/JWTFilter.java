package sequence.sequence_member.member.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import sequence.sequence_member.global.exception.AuthException;
import sequence.sequence_member.global.exception.ExpiredTokenException;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;

import java.io.IOException;
import java.io.PrintWriter;

//요청에 대해서 한번만 동작하는 OncePerRequestFilter를 상속받는다
public class JWTFilter extends OncePerRequestFilter {

    JWTUtil jwtUtil;
    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }
    //내부 필터에 대한 특정 구현을 진행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 access 에 담긴 토큰을 꺼낸다
        String accessToken = request.getHeader("access");

        //토큰이 없다면 다음 필터로 넘김
        if(accessToken == null){
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try{
            jwtUtil.isExpired(accessToken);
        }catch (ExpiredJwtException e){
            // 만약 토큰이 만료되면 클라이언트에 에러 메시지와 상태 코드를 반환
            response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED); // 401 상태 코드
            response.setContentType("application/json"); // 응답 타입을 JSON으로 설정
            response.setCharacterEncoding("UTF-8");

            // JSON 형식으로 에러 메시지 작성
            String errorMessage = "{\"status\": 40202, \"error\": \"JWT token has expired\", \"message\": \"액세스토큰이 만료되었습니다. 로그인을 다시하세요.\"}";
            response.getWriter().write(errorMessage);
            response.getWriter().flush();

            return; // 필터 체인 진행하지 않음
        }

        //토큰이 access 토큰인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //username 값을 획득
        String username = jwtUtil.getUsername(accessToken);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(username);

        CustomUserDetails customUserDetails = new CustomUserDetails(memberEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request,response);
    }
}
