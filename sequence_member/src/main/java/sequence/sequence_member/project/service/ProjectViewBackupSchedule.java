package sequence.sequence_member.project.service;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectViewBackupSchedule {

    private final StringRedisTemplate redisTemplate;
    private final ProjectRepository projectRepository;

    @Scheduled(fixedRate = 60000*10*6) //분*10 = 1시간마다 실행(todo - test를 위해 짧은 주리고 동기화중. 추후 수정)
    @Transactional
    public void projectViewBackUpToDB(){
        log.info("Redis 조회수를 DB로 백업 중...");

        // Redis에서 조회수 키 조회 (viewCount:postId 형태)
        Set<String> keys = redisTemplate.keys("viewCount:*");

        if (keys == null || keys.isEmpty()) {
            log.info("저장된 조회수 데이터가 없습니다.");
            return;
        }

        for (String key : keys) {
            try {
                // key에서 postId 추출
                Long projectId = Long.parseLong(key.replace("viewCount:", ""));

                // Redis에서 조회수 가져오기
                String redisViewCountStr = redisTemplate.opsForValue().get(key);
                int redisViewCount = (redisViewCountStr != null) ? Integer.parseInt(redisViewCountStr) : 0;

                // DB 업데이트
                Optional<Project> project = projectRepository.findById(projectId);

                //만약 해당 프로젝트가 존재하지 않다면 무시
                if(project.isEmpty()) {
                    log.error("redis에 존재하지 않는 projectId가 존재합니다 : {}", projectId);
                    //todo - redis에 해당 projectId 정보 삭제
                    continue;
                }
                project.get().setViews(redisViewCount);
                projectRepository.save(project.get());
                log.info("게시글 [{}] 조회수 업데이트 완료! 총 조회수: {}", projectId, redisViewCount);
            } catch (Exception e) {
                log.error("조회수 동기화 중 오류 발생: " + e.getMessage());
            }
        }
    }
}
