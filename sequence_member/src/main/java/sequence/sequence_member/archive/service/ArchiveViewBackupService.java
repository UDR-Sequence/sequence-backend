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
import sequence.sequence_member.archive.utill.RedisKeyManager;

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

        Set<String> keys = redisTemplate.keys(RedisKeyManager.ARCHIVE_VIEW_COUNT_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            log.info("저장된 조회수 데이터가 없습니다.");
            return;
        }

        for (String key : keys) {
            try {
                Long archiveId = RedisKeyManager.extractId(key);
                
                Optional<Archive> archive = archiveRepository.findById(archiveId);
                if (archive.isEmpty()) {
                    log.info("삭제된 아카이브의 Redis 데이터 정리: archiveId={}", archiveId);
                    // 삭제된 아카이브의 Redis 데이터 정리
                    cleanupDeletedArchiveData(archiveId);
                    continue;
                }

                String redisViewCountStr = redisTemplate.opsForValue().get(key);
                int redisViewCount = (redisViewCountStr != null) ? Integer.parseInt(redisViewCountStr) : 0;

                archive.get().setView(redisViewCount);
                archiveRepository.save(archive.get());
                log.info("아카이브 [{}] 조회수 업데이트 완료! 총 조회수: {}", archiveId, redisViewCount);
            } catch (Exception e) {
                log.error("조회수 동기화 중 오류 발생: {}, key: {}", e.getMessage(), key);
            }
        }
    }

    // 삭제된 아카이브의 Redis 데이터 정리
    private void cleanupDeletedArchiveData(Long archiveId) {
        try {
            String viewCountKey = RedisKeyManager.getArchiveViewCountKey(archiveId);
            String viewedKey = RedisKeyManager.getArchiveViewedKey(archiveId);
            
            redisTemplate.delete(viewCountKey);
            redisTemplate.delete(viewedKey);
            
            log.info("삭제된 아카이브의 Redis 데이터 정리 완료: archiveId={}", archiveId);
        } catch (Exception e) {
            log.error("Redis 데이터 정리 중 오류 발생: {}, archiveId={}", e.getMessage(), archiveId);
        }
    }
} 