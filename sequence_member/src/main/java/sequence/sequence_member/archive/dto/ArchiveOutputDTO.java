package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveOutputDTO {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private Category category;
    private Period period;
    private Status status;
    private String thumbnail;
    private String link;
    private List<String> skills;
    private List<String> imgUrl;
    private Integer view;
    private Integer bookmark;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
} 
