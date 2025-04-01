package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sequence.sequence_member.archive.utill.RedisKeyManager;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArchiveViewService {
    private final StringRedisTemplate redisTemplate;
    private static final long VIEW_EXPIRATION_TIME = 60 * 60; // 1시간 유지

    public int getViewsFromRedis(HttpServletRequest request, Long archiveId) {
        String clientId = getClientIP(request) + getUserAgent(request).hashCode();
        String viewedKey = RedisKeyManager.getArchiveViewedKey(archiveId);
        String viewCountKey = RedisKeyManager.getArchiveViewCountKey(archiveId);

        redisTemplate.opsForValue().setIfAbsent(viewCountKey, "0");

        if (!isAlreadyViewed(archiveId, clientId)) {
            redisTemplate.opsForValue().increment(viewCountKey);
            redisTemplate.opsForSet().add(viewedKey, clientId);
            redisTemplate.expire(viewedKey, Duration.ofSeconds(VIEW_EXPIRATION_TIME));
        }

        return Integer.parseInt(Objects.requireNonNullElse(redisTemplate.opsForValue().get(viewCountKey), "0"));
    }

    private boolean isAlreadyViewed(Long archiveId, String clientId) {
        String key = RedisKeyManager.getArchiveViewedKey(archiveId);
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