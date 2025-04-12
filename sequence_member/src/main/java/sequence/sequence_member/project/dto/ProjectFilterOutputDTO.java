package sequence.sequence_member.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import sequence.sequence_member.global.utils.DataConvertor;
import sequence.sequence_member.project.entity.Project;

@Data
@Builder
public class ProjectFilterOutputDTO {
    private Long id; // 프로젝트 id
    private String title; // 글 제목
    private String writer; // 프로젝트 작성자 닉메임
    private LocalDate createdDate; // 프로젝트 작성일
    private List<String> roles; // 역할

    public static ProjectFilterOutputDTO toProjectFilterOutputDTO(Project project){
        return ProjectFilterOutputDTO.builder()
            .id(project.getId())
            .title(project.getTitle())
            .writer(project.getWriter().getNickname())
            .createdDate(project.getCreatedDateTime().toLocalDate())
            .roles(DataConvertor.stringToList(project.getRoles()))
            .build();
    }
}
