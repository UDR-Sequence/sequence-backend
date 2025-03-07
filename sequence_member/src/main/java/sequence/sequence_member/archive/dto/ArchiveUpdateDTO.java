package sequence.sequence_member.archive.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import sequence.sequence_member.global.enums.enums.Category;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ArchiveUpdateDTO {
    @NotEmpty(message = "제목을 입력해주세요.")
    @Length(min = 1, max = 40, message = "제목은 40자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "설명을 입력해주세요.")
    @Length(min = 1, max = 450, message = "설명은 450자 이하로 입력해주세요.")
    private String description;

    @NotNull(message = "시작일을 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    private LocalDate endDate;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    private String thumbnail;
    private String link;

    @NotEmpty(message = "관련 기술을 선택해주세요.")
    @Size(max = 20, message = "관련 기술은 20개 이하로 선택해주세요.")
    private List<String> skills;

    private List<String> imgUrls;

    @NotEmpty(message = "팀원 정보를 입력해주세요.")
    private List<ArchiveMemberDTO> archiveMembers;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArchiveMemberDTO {
        private String username;
        private String role;
    }
}
