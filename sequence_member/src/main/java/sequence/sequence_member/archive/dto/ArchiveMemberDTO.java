package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import sequence.sequence_member.member.dto.MemberDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveMemberDTO {
    private Long archiveMemberId;

    // MemberDTO로 변환
    private MemberDTO memberId;

    // ArchiveDTO로 변환
    private ArchiveDTO archiveId;
}
