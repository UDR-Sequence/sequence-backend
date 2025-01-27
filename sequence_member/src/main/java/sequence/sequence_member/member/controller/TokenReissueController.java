package sequence.sequence_member.member.controller;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.RefreshRepository;
import sequence.sequence_member.member.service.TokenReissueService;

@RestController
public class TokenReissueController {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final TokenReissueService tokenReissueService;

    public TokenReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository, TokenReissueService tokenReissueService){
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.tokenReissueService = tokenReissueService;
    }

    @PostMapping("/api/token")
    public ResponseEntity<?> TokenReissue(HttpServletRequest request, HttpServletResponse response) {

        //refresh token 획득
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh")){
                refresh = cookie.getValue();
            }
        }

        if(refresh == null){
            //응답 상태 반환
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //refreshToken 만료 체크
        try{
            jwtUtil.isExpired(refresh);
        }catch(ExpiredJwtException e){

            //상태 코드 반환
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")){
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //토큰이 DB에 존재하는지 파악
        Boolean isExist = refreshRepository.existsByRefresh(refresh);

        //토큰이 존재하지 않는다면,
        if(!isExist){
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);

        //새로운 토큰 생성
        String newAccess = jwtUtil.createJwt("access", username, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, 86400000L);

        //기존 refresh 토큰을 DB에서 삭제 후 새로운 refresh 토큰을 저장한다
        refreshRepository.deleteByRefresh(refresh);
        tokenReissueService.RefreshTokenSave(username, newRefresh, 86400000L);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh",newRefresh));

        //응답 헤더에 엑세스 토큰을 넣어서 리턴
        return new ResponseEntity<>(HttpStatus.OK);

    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
