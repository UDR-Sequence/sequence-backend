package sequence.sequence_member.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.project.service.RedisTestService;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class RedisTestController {
    private final RedisTestService redisTestService;

    @GetMapping("/redis")
    public String testRedis() {
        log.info("레디스 테스트 요청 : /test/redis GET request 발생");

        redisTestService.testRedisConnection();

        return "Redis 테스트 완료!";
    }
}
