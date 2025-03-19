package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//todo - 페이지 정보도 함께 return 해줘야함.
// 응답 데이터를 담을 DTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyActivityResponseDTO {
    private List<PostDTO> writtenPosts;
    private List<PostDTO> bookmarkedPosts;
}
