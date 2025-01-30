package sequence.sequence_member.project.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentOutputDTO {
    private CommentDTO parentComment;
    private List<CommentDTO> childComments;

    public CommentOutputDTO(CommentDTO parentComment) {
        this.parentComment = parentComment;
        this.childComments = new ArrayList<>();
    }

    public void addChildComment(CommentDTO comment) {
        this.childComments.add(comment);
    }
}
