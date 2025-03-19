package sequence.sequence_member.archive.dto;

import java.sql.Date;
import lombok.Getter;
import sequence.sequence_member.archive.entity.Archive;

@Getter
public class UserArchiveDTO {

    private Long archiveId;
    private String archiveTitle;
    private Date createDate;

    public UserArchiveDTO(Archive archive){
        this.archiveId = archive.getId();
        this.archiveTitle = archive.getTitle();
        this.createDate = Date.valueOf(archive.getCreatedDateTime().toLocalDate());
    }
}
