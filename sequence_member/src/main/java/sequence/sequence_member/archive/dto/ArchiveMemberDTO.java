package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveMemberDTO {
    private String nickname;
    private String profileImg;
}
