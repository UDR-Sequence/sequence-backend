package sequence.sequence_member.project.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.annotation.MethodDescription;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectViewService {
    private final ProjectRepository projectRepository;
    private final StringRedisTemplate redisTemplate;
    private static final long VIEW_EXPIRATION_TIME = 60 * 60; // 1시간 유지

    @MethodDescription(description = "Redis에서 조회수를 가져오는 로직")
    public int getViewsFromRedis(HttpServletRequest request, Long postId){

        // IP + User-Agent로 조회이력을 확인할 고유 Value값 생성.
        String clientId = getClientIP(request)+getUserAgent(request).hashCode();

        //조회 이력을 확인하기 위한 key. redis에 저장된 key값. viewed3:Set<ClientId> 가 key:value형태로 저장됨.
        String key = "viewed:" + postId;

        //조회수 확인 및 증가를 위한 key
        String viewedKey = "viewCount:" + postId;

        // 조회수가 없으면 0으로 초기화
        redisTemplate.opsForValue().setIfAbsent(viewedKey, "0");

        // clientId를 활용하여 조회 이력이 없을경우. 조회수 증가 및 확인 처리.
        if(!isAlreadyViewed(postId,clientId)){

            // 조회수 증가
            redisTemplate.opsForValue().increment(viewedKey);

            //조회 기록 저장
            redisTemplate.opsForSet().add(key, clientId);
            redisTemplate.expire(key, Duration.ofSeconds(VIEW_EXPIRATION_TIME)); // 1시간 후 자동 삭제
        }

        return Integer.parseInt(Objects.requireNonNullElse(redisTemplate.opsForValue().get(viewedKey),"0"));
    }


    @MethodDescription(description = "이미 조회한 글인지 확인")
    private boolean isAlreadyViewed(Long postId, String clientId){
        String key = "viewed:" + postId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, clientId));
    }

    @MethodDescription(description = "user의 ip 조회.")
    private String getClientIP(HttpServletRequest request){
        String ip = request.getHeader("X-Forward-For");

        if(ip == null || ip.isBlank()){
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    @MethodDescription(description = "User의 user-agent정보 조회. 없으면 빈문자열")
    private String getUserAgent(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) {
            userAgent="";
        }

        return userAgent;
    }
}
