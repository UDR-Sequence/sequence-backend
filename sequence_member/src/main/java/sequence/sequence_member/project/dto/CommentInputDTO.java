package sequence.sequence_member.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentInputDTO {

    @NotNull(message = "댓글 내용을 입력해주세요.")
    private String content;

    private Long parentCommentId;

}
