package sequence.sequence_member.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.project.dto.ProjectInputDTO;
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
}
