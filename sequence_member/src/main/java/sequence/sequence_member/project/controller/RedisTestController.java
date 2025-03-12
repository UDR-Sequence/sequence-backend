package sequence.sequence_member.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.project.service.RedisTestService;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class RedisTestController {
    private final RedisTestService redisTestService;

    @GetMapping("/redis")
    public String testRedis() {
        redisTestService.testRedisConnection();
        return "Redis 테스트 완료!";
    }
}
