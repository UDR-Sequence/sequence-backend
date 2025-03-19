package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

// 게시글 정보를 담을 DTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String title;
    private Long articleId;
    private Date createdDate;
    private int numberOfComments;
}