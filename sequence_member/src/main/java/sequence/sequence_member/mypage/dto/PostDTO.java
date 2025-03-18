package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

// 게시글 정보를 담을 DTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String title;
    private String type; // "project" 또는 "archive"
    private Long id;
    private Date createdDate;
    private int numberOfComments;
}