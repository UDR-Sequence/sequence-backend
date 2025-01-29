package sequence.sequence_member.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.dto.ProjectOutputDTO;
import sequence.sequence_member.project.dto.ProjectUpdateDTO;
import sequence.sequence_member.project.service.ProjectService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping()
    public ResponseEntity<ApiResponseData<String>> registerProject(@Valid @RequestBody ProjectInputDTO projectInputDTO, @AuthenticationPrincipal
                                                                   CustomUserDetails customUserDetails) {
        projectService.createProject(projectInputDTO, customUserDetails);
        return ResponseEntity.ok(ApiResponseData.success(null, "프로젝트 등록 성공"));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<ProjectOutputDTO>> getProject(@PathVariable Long projectId){
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회 성공", projectService.getProject(projectId)));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<ProjectOutputDTO>> updateProject(@PathVariable Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody
                                                                 ProjectUpdateDTO projectUpdateDTO){
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 수정 성공",projectService.updateProject(projectId, customUserDetails, projectUpdateDTO)));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<String>> deleteProject(@PathVariable Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        projectService.deleteProject(projectId, customUserDetails);
        return ResponseEntity.ok().body(ApiResponseData.success(null, "프로젝트 삭제 성공"));
    }

}
