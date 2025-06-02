package sequence.sequence_member.project.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectFilterResultDTO {
  private int totalPages;
  private Long totalProjects;
  private List<ProjectFilterOutputDTO> projects;

  public static ProjectFilterResultDTO of(int totalPages, Long totalElements, List<ProjectFilterOutputDTO> projectFilterOutputDTOS){
    return ProjectFilterResultDTO.builder()
        .totalPages(totalPages)
        .totalProjects(totalElements)
        .projects(projectFilterOutputDTOS)
        .build();
  }


}
