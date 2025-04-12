package sequence.sequence_member.member.service;
import org.springframework.stereotype.Service;
import sequence.sequence_member.member.entity.RefreshEntity;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.RefreshRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class TokenReissueService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public TokenReissueService(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    // 리프레시토큰 저장 로직
    public void RefreshTokenSave(String username, String refresh, Long expiredMs) {
        // 밀리초 → Instant → LocalDate 변환
        LocalDate expirationDate = Instant.ofEpochMilli(System.currentTimeMillis() + expiredMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(expirationDate.toString()); // String 저장이 필요 없다면 타입도 LocalDate로 바꿔줘야 함

        refreshRepository.save(refreshEntity);
    }
}
