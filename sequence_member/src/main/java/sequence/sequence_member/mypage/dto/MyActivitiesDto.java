package sequence.sequence_member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 사용자의 활동이력 DTO
 *
 * 마이페이지 화면에서 '내 활동'에 해당하는 객체
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyActivitiesDto {
    private MyProjectDTO myProject;
    private MyArchiveDTO myArchive;

    @Getter
    @AllArgsConstructor
    public static class MyProjectDTO {
        private List<PostDTO> writtenPosts;
        private List<PostDTO> bookmarkedPosts;
    }

    @Getter
    @AllArgsConstructor
    public static class MyArchiveDTO {
        private List<PostDTO> writtenPosts;
        private List<PostDTO> bookmarkedPosts;
    }

    @Getter
    @AllArgsConstructor
    public static class PostDTO {
        private String title;
        private Long articleId;
        private Date createdDate;
        private int numberOfComments;
    }
}
