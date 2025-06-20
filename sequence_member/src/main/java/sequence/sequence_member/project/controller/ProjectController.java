package sequence.sequence_member.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import sequence.sequence_member.project.dto.ProjectFilterResultDTO;
import sequence.sequence_member.project.dto.ProjectInputDTO;
import sequence.sequence_member.project.dto.ProjectOutputDTO;
import sequence.sequence_member.project.dto.ProjectUpdateDTO;
import sequence.sequence_member.project.service.ProjectBookmarkService;
import sequence.sequence_member.project.service.ProjectCreateService;
import sequence.sequence_member.project.service.ProjectGetService;
import sequence.sequence_member.project.service.ProjectService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectCreateService projectCreateService;
    private final ProjectGetService projectGetService;
    private final ProjectBookmarkService projectBookmarkService;

    @PostMapping()
    public ResponseEntity<ApiResponseData<String>> registerProject(@Valid @RequestBody ProjectInputDTO projectInputDTO, @AuthenticationPrincipal
                                                                   CustomUserDetails customUserDetails) {
        log.info("프로젝트 등록 요청 : /api/projects POST request 발생");

        projectCreateService.createProject(projectInputDTO, customUserDetails.getUsername());

        return ResponseEntity.ok(ApiResponseData.success(null, "프로젝트 등록 성공"));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<ProjectOutputDTO>> getProject(@PathVariable("projectId") Long projectId, HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("프로젝트 상세 조회 요청 : /api/projects/{projectId} GET request 발생");

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회 성공", projectGetService.getProject(projectId, request, customUserDetails)));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<ProjectOutputDTO>> updateProject(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody
                                                                 ProjectUpdateDTO projectUpdateDTO, HttpServletRequest request){
        log.info("프로젝트 수정 요청 : /api/projects/{projectId} PUT request 발생");

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 수정 성공",projectService.updateProject(projectId, customUserDetails, projectUpdateDTO,request)));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponseData<String>> deleteProject(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("프로젝트 삭제 요청 : /api/projects/{projectId} DELETE request 발생");

        projectService.deleteProject(projectId, customUserDetails);

        return ResponseEntity.ok().body(ApiResponseData.success(null, "프로젝트 삭제 성공"));
    }

    /**
     * 북마크 관련 엔드포인트
     * */
    // 북마크 등록
    @PostMapping("/{projectId}/bookmark")
    public ResponseEntity<ApiResponseData<String>> addProjectBookmark(@PathVariable("projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("프로젝트 북마크 등록 요청 : /api/projects/{projectId}/bookmark POST request 발생");

        return ResponseEntity.ok().body(ApiResponseData.success(null,projectBookmarkService.addBookmark(customUserDetails, projectId)));
    }
    // 북마크 삭제
    @DeleteMapping("/{projectId}/bookmark")
    public ResponseEntity<ApiResponseData<String>> removeProjectBookmark(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("프로젝트 북마크 삭제 요청 : /api/projects/{projectId}/bookmark DELETE request 발생");

        return ResponseEntity.ok().body(ApiResponseData.success(null, projectBookmarkService.removeBookmark(customUserDetails, projectId)));
    }

    @GetMapping("/filter/keyword")
    public ResponseEntity<ApiResponseData<ProjectFilterResultDTO>> filterKeyword(@RequestParam(name = "category", required = false) Category category,
                                                                                 @RequestParam(name = "periodKey",required = false) String periodKey,
                                                                                 @RequestParam(name = "roles",required = false) String roles,
                                                                                 @RequestParam(name = "skills",required = false) String skills,
                                                                                 @RequestParam(name = "meetingOption",required = false) MeetingOption meetingOption,
                                                                                 @RequestParam(name = "step",required = false) Step step,
                                                                                 @RequestParam(name="sortBy", required = false, defaultValue = "createdDateTime") String sortBy,
                                                                                 @RequestParam(name="page", required = false, defaultValue = "0") int page,
                                                                                 @RequestParam(name="size", required = false, defaultValue = "12") int size){
        log.info("프로젝트 키워트 필터링 요청 : /api/projects/filter/keyword GET request 발생");

        Page<ProjectFilterOutputDTO> projectFilterOutputDTOS = projectService.getProjectsByKeywords(category,periodKey,roles,skills,meetingOption,step,sortBy,page,size);

        //조회된 프로젝트가 하나도 없는 경우
        if(projectFilterOutputDTOS.isEmpty()){
            return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "해당 키워드와 일치하는 프로젝트가 없습니다.",ProjectFilterResultDTO.of(0, 0L,projectFilterOutputDTOS.getContent())));
        }

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회가 완료되었습니다.",ProjectFilterResultDTO.of(
            projectFilterOutputDTOS.getTotalPages(), projectFilterOutputDTOS.getTotalElements(),projectFilterOutputDTOS.getContent())));
    }

    @GetMapping("/filter/search")
    public ResponseEntity<ApiResponseData<ProjectFilterResultDTO>> filterSearch(@RequestParam(name="title") String title,
                                                                                      @RequestParam(name="sortBy", required = false, defaultValue = "createdDateTime") String sortBy,
                                                                                      @RequestParam(name="page", required = false, defaultValue = "0") int page,
                                                                                      @RequestParam(name="size", required = false, defaultValue = "12") int size){
        log.info("프로젝트 검색 필터링 요청 : /api/projects/filter/search GET request 발생");

        Page<ProjectFilterOutputDTO> projectFilterOutputDTOS = projectService.getProjectsBySearch(title, sortBy, page, size);

        if(projectFilterOutputDTOS.isEmpty()){
            return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "검색어와 일치하는 프로젝트가 없습니다.",ProjectFilterResultDTO.of(0, 0L,projectFilterOutputDTOS.getContent())));
        }

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "프로젝트 조회가 완료되었습니다.",ProjectFilterResultDTO.of(projectFilterOutputDTOS.getTotalPages(), projectFilterOutputDTOS.getTotalElements(),projectFilterOutputDTOS.getContent())));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponseData<List<ProjectFilterOutputDTO>>> findProjects(){
        log.info("프로젝트 전체 조회 요청 : /api/projects/list GET request 발생");

        List<ProjectFilterOutputDTO> projectEntities = new ArrayList<>(projectService.getAllProjects());

        if(projectEntities.isEmpty()){
            return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "조회된 프로젝트가 없습니다.",projectEntities));
        }

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "모든 프로젝트 조회가 완료되었습니다.",projectEntities));
    }


}
