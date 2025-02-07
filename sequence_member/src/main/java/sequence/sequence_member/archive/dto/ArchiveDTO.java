package sequence.sequence_member.archive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveDTO {
    private Long archiveId;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "설명은 필수 입력 값입니다.")
    private String description;

    @NotBlank(message = "기간은 필수 입력 값입니다.")
    private String duration;

    @NotBlank(message = "분야는 필수 입력 값입니다.")
    private String field;

    @NotNull(message = "상태는 필수 입력 값입니다.")
    private Byte status;

    private List<ArchiveMemberDTO> archiveMembers;
}
