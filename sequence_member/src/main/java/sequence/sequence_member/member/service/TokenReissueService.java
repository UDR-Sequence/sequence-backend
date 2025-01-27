package sequence.sequence_member.member.service;

import org.springframework.stereotype.Service;
import sequence.sequence_member.member.entity.RefreshEntity;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.RefreshRepository;

import java.util.Date;

@Service
public class TokenReissueService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public TokenReissueService(JWTUtil jwtUtil, RefreshRepository refreshRepository){
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    //리프레시토큰 저장 로직
    public void RefreshTokenSave(String username, String refresh,Long expiredMs){
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

}
