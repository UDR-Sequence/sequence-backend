package sequence.sequence_member.project.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProjectFilterOutputDTO {
    private Long id; // 프로젝트 id
    private String title; // 글 제목
    private String writer; // 프로젝트 작성자 닉메임
    private Date createdDate; // 프로젝트 작성일
    private List<String> roles; // 역할

}
