package sequence.sequence_member.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.MeetingOption;
import sequence.sequence_member.global.enums.enums.Step;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.project.dto.ProjectFilterOutputDTO;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.dto.ProjectOutputDTO;
import sequence.sequence_member.project.dto.ProjectUpdateDTO;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.service.ProjectService;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<ApiResponseData<ProjectOutputDTO>> getProject(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회 성공", projectService.getProject(projectId)));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<ProjectOutputDTO>> updateProject(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody
                                                                 ProjectUpdateDTO projectUpdateDTO){
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 수정 성공",projectService.updateProject(projectId, customUserDetails, projectUpdateDTO)));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<String>> deleteProject(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        projectService.deleteProject(projectId, customUserDetails);
        return ResponseEntity.ok().body(ApiResponseData.success(null, "프로젝트 삭제 성공"));
    }

    @GetMapping("/filter/keyword")
    public ResponseEntity<ApiResponseData<List<ProjectFilterOutputDTO>>> filterKeyword(@RequestParam(name = "category", required = false) Category category,
                                                        @RequestParam(name = "periodKey",required = false) String periodKey,
                                                        @RequestParam(name = "roles",required = false) String roles,
                                                        @RequestParam(name = "skills",required = false) String skills,
                                                        @RequestParam(name = "meetingOption",required = false) MeetingOption meetingOption,
                                                        @RequestParam(name = "step",required = false) Step step){
        List<ProjectFilterOutputDTO> projectEntities = new ArrayList<>(projectService.getProjectsByKeywords(category,periodKey,roles,skills,meetingOption,step));

        //조회된 프로젝트가 하나도 없는 경우
        if(projectEntities.isEmpty()){
            return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "해당 필터와 일치하는 프로젝트가 없습니다.",projectEntities));
        }

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회가 완료되었습니다.",projectEntities));
    }

    @GetMapping("/filter/search")
    public ResponseEntity<ApiResponseData<List<ProjectFilterOutputDTO>>> filterSearch(@RequestParam(name="title") String title){
        List<ProjectFilterOutputDTO> projectEntities = new ArrayList<>(projectService.getProjectsBySearch(title));
        if(projectEntities.isEmpty()){
            return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "검색어와 일치하는 프로젝트가 없습니다.",projectEntities));
        }
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회가 완료되었습니다.",projectEntities));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponseData<List<ProjectFilterOutputDTO>>> findProjects(){
        List<ProjectFilterOutputDTO> projectEntities = new ArrayList<>(projectService.getAllProjects());

        if(projectEntities.isEmpty()){
            return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "조회된 프로젝트가 없습니다.",projectEntities));
        }

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "모든 프로젝트 조회가 완료되었습니다.",projectEntities));
    }


}
