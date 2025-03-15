package sequence.sequence_member.alarm.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sequence.sequence_member.archive.dto.UserArchiveDTO;
import sequence.sequence_member.member.dto.InviteProjectOutputDTO;

@Getter
@AllArgsConstructor
public class ProjectArchiveAlarmDTO {
    List<InviteProjectOutputDTO> inviteProjectOutputList;
    List<UserArchiveDTO> userArchiveList;
}
