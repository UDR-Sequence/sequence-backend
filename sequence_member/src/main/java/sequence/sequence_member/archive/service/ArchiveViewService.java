package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import sequence.sequence_member.archive.repository.ArchiveRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArchiveViewService {
    private final ArchiveRepository archiveRepository;
    private final StringRedisTemplate redisTemplate;
    private static final long VIEW_EXPIRATION_TIME = 60 * 60; // 1시간 유지

    public int getViewsFromRedis(HttpServletRequest request, Long archiveId) {
        // IP + User-Agent로 조회이력을 확인할 고유 Value값 생성
        String clientId = getClientIP(request) + getUserAgent(request).hashCode();

        // 조회 이력을 확인하기 위한 key
        String key = "viewed:archive:" + archiveId;

        // 조회수 확인 및 증가를 위한 key
        String viewedKey = "viewCount:archive:" + archiveId;

        // 조회수가 없으면 0으로 초기화
        redisTemplate.opsForValue().setIfAbsent(viewedKey, "0");

        // 조회 이력이 없을 경우 조회수 증가 및 확인 처리
        if (!isAlreadyViewed(archiveId, clientId)) {
            // 조회수 증가
            redisTemplate.opsForValue().increment(viewedKey);

            // 조회 기록 저장 (1시간 후 자동 삭제)
            redisTemplate.opsForSet().add(key, clientId);
            redisTemplate.expire(key, Duration.ofSeconds(VIEW_EXPIRATION_TIME));
        }

        return Integer.parseInt(Objects.requireNonNullElse(redisTemplate.opsForValue().get(viewedKey), "0"));
    }

    private boolean isAlreadyViewed(Long archiveId, String clientId) {
        String key = "viewed:archive:" + archiveId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, clientId));
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forward-For");
        return (ip == null || ip.isBlank()) ? request.getRemoteAddr() : ip;
    }

    private String getUserAgent(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getHeader("User-Agent"), "");
    }
} 