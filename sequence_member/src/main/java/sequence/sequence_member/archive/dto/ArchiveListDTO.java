package sequence.sequence_member.archive.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ArchiveListDTO {
    private List<ArchiveSimpleDTO> archives;
    private int totalPages;
    private long totalElements;  // 전체 아카이브 수

    @Getter
    @Builder
    public static class ArchiveSimpleDTO {
        private Long id;
        private String title;
        private String writerNickname;
        private String thumbnail;
        private int commentCount;
        private int view;  // 조회수 추가
        private int bookmarkCount;  // 북마크 수 추가
        private LocalDateTime createdDateTime;
    }
} 