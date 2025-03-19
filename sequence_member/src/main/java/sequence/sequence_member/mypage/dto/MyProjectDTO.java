package sequence.sequence_member.mypage.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyProjectDTO {
    private List<PostDTO> writtenPosts;
    private List<PostDTO> bookmarkedPosts;
}
