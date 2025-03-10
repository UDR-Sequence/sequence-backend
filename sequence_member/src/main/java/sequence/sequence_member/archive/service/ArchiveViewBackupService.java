package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveViewBackupService {
    private final StringRedisTemplate redisTemplate;
    private final ArchiveRepository archiveRepository;

    @Scheduled(fixedRate = 600000) // 10분마다 실행
    @Transactional
    public void archiveViewBackUpToDB() {
        log.info("Redis 조회수를 DB로 백업 중...");

        Set<String> keys = redisTemplate.keys("viewCount:archive:*");
        if (keys == null || keys.isEmpty()) {
            log.info("저장된 조회수 데이터가 없습니다.");
            return;
        }

        for (String key : keys) {
            try {
                Long archiveId = Long.parseLong(key.replace("viewCount:archive:", ""));
                String redisViewCountStr = redisTemplate.opsForValue().get(key);
                int redisViewCount = (redisViewCountStr != null) ? Integer.parseInt(redisViewCountStr) : 0;

                Optional<Archive> archive = archiveRepository.findById(archiveId);
                if (archive.isEmpty()) {
                    log.error("존재하지 않는 archiveId: {}", archiveId);
                    continue;
                }

                archive.get().setView(redisViewCount);
                archiveRepository.save(archive.get());
                log.info("아카이브 [{}] 조회수 업데이트 완료! 총 조회수: {}", archiveId, redisViewCount);
            } catch (Exception e) {
                log.error("조회수 동기화 중 오류 발생: " + e.getMessage());
            }
        }
    }
} 