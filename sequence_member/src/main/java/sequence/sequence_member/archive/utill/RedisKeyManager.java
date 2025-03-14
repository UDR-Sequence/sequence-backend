package sequence.sequence_member.archive.utill;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyManager {
    // 아카이브 관련 키
    public static final String ARCHIVE_VIEW_COUNT_PREFIX = "viewCount:archive:";
    public static final String ARCHIVE_VIEWED_PREFIX = "viewed:archive:";

    // 아카이브 키 생성 메서드
    public static String getArchiveViewCountKey(Long archiveId) {
        return ARCHIVE_VIEW_COUNT_PREFIX + archiveId;
    }

    public static String getArchiveViewedKey(Long archiveId) {
        return ARCHIVE_VIEWED_PREFIX + archiveId;
    }

    // ID 추출 메서드
    public static Long extractId(String key) {
        String[] parts = key.split(":");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
