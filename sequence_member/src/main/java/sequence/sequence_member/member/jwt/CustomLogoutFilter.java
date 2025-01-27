package sequence.sequence_member.member.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import sequence.sequence_member.member.repository.RefreshRepository;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository){
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //logout 경로로 요청이 들어왔는지 검사하고 안되었으면 다음 필터 실행
        String requestUri = request.getRequestURI();
        if(!requestUri.matches("^\\/api\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        //요청 방식이 POST인지 검사하고 아니면 다음 필터 실행
        String requestMethod = request.getMethod();
        if(!requestMethod.equals("POST")){
            filterChain.doFilter(request,response);
            return;
        }

        //refresh 토큰을 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        System.out.println(cookies);
        if(cookies != null){
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("refresh")){
                    refresh = cookie.getValue();
                    break;
                }
            }
        }


        //refresh 토큰 체크
        if(refresh == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //refresh token 만료되었는지 확인
        try{
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e){
            //만료 되었으면 상태 반환
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Token has expired");
            return;
        }

        //토큰이 refresh 인지 확인
        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refresh")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 refresh 토큰이 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if(!isExist){

            //에러메세지나 적절한 만료되었다는 메세지를 전달해주면 된다
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //refresh db에서 토큰 제거
        refreshRepository.deleteByRefresh(refresh);

        //Refresh 토큰 삭제 후, cookie값을 null로 처리해준다.
        //유효시간 값과 path 값, credential 값 등 여러가지를 처리해준다.
        Cookie cookie = new Cookie("refresh",null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);


    }


}
