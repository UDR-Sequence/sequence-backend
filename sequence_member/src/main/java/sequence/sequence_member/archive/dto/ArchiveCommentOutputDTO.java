package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveCommentOutputDTO {
    private CommentDTO parentComment;
    private List<CommentDTO> childComments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDTO {
        private Long id;
        private String writer;
        private String writerProfileImg;
        private String content;
        private boolean isDeleted;
        private LocalDateTime createdDateTime;
        private LocalDateTime modifiedDateTime;
    }

    public ArchiveCommentOutputDTO(CommentDTO parentComment) {
        this.parentComment = parentComment;
        this.childComments = new ArrayList<>();
    }

    public void addChildComment(CommentDTO comment) {
        this.childComments.add(comment);
    }
} 