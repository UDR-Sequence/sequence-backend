package sequence.sequence_member.archive.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import sequence.sequence_member.archive.entity.ArchiveComment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String writer;    // 작성자 아이디
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDTO> replies;  // 대댓글 목록

    // Entity -> DTO 변환 메서드
    public static CommentResponseDTO from(ArchiveComment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent().trim())
                .writer(comment.getWriter().trim())
                .isDeleted(comment.isDeleted())
                .createdAt(comment.getCreatedDateTime())
                .modifiedAt(comment.getModifiedDateTime())
                .replies(new ArrayList<>())  // null 방지
                .build();
    }
} 