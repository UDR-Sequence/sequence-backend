package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveOutputDTO {
    private Long id;
    private String writerUsername;
    private String writerNickname;
    private String writerProfileImg;
    private String title;
    private String description; 
    private LocalDate startDate;
    private LocalDate endDate;
    private String duration;
    private Category category;
    private String thumbnail;
    private String link;
    private List<String> skills;
    private List<String> imgUrls;
    private Integer view;
    private boolean isBookmarked;
    private int bookmarkCount;
    private List<ArchiveMemberDTO> members;
    private List<ArchiveCommentOutputDTO> comments;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;

    @Getter
    @Builder
    public static class ArchiveMemberDTO {
        private String username;
        private String nickname;
        private String profileImg;
    }
} 
