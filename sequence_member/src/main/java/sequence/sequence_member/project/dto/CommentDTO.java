package sequence.sequence_member.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDTO {
    private Long id;
    private String writer; // 작성자_닉네임
    private String content; // 내용
    private LocalDateTime createdLocalDateTime; // 작성일
}
