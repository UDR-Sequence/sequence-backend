package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyArchiveDTO {
    private List<PostDTO> writtenPosts;
    private List<PostDTO> bookmarkedPosts;
}
