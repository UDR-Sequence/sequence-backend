package sequence.sequence_member.archive.dto;

import java.util.Date;
import lombok.Getter;
import sequence.sequence_member.archive.entity.Archive;

@Getter
public class UserArchiveDTO {

    private Long projectId;
    private String archiveTitle;
    private Date createDate;

    public UserArchiveDTO(Archive archive){

    }
}
