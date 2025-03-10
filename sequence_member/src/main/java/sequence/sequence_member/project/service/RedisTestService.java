package sequence.sequence_member.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTestService {
    private final StringRedisTemplate redisTemplate;

    public void testRedisConnection() {
        redisTemplate.opsForValue().set("testKey", "Hello, Redis!");
        String value = redisTemplate.opsForValue().get("testKey");
        System.out.println("Redis에서 가져온 값: " + value);
    }
}