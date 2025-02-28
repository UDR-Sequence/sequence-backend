package sequence.sequence_member.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDto;
import sequence.sequence_member.archive.entity.TeamEvaluation;
import sequence.sequence_member.archive.service.TeamEvaluationService;
import sequence.sequence_member.global.response.ApiResponseData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import sequence.sequence_member.member.dto.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.global.enums.enums.Status;
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDto;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class TeamEvaluationController {

    private final TeamEvaluationService teamEvaluationService;

    @PostMapping("/{archiveId}/evaluations")
    public ResponseEntity<?> createTeamEvaluation(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TeamEvaluationRequestDto requestDto) {
            
        teamEvaluationService.createTeamEvaluation(archiveId, userDetails.getUsername(), requestDto);
        return ResponseEntity
                .status(Code.CREATED.getStatus())
                .body(ApiResponseData.of(Code.CREATED.getCode(), "팀원 평가가 완료되었습니다.", null));
    }

    @GetMapping("/{archiveId}/evaluations")
    public ResponseEntity<?> getTeamEvaluations(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
    
        List<TeamEvaluationResponseDto> evaluations = teamEvaluationService.getTeamEvaluations(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(evaluations));
    }

    @GetMapping(
        value = "/{archiveId}/evaluations/status",
        produces = "application/json"
    )
    public ResponseEntity<ApiResponseData<Map<String, Status>>> getEvaluationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long archiveId) {
        Map<String, Status> evaluationStatus = teamEvaluationService.getEvaluationStatus(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(evaluationStatus));
    }

    @GetMapping("/{archiveId}/team-members")
    public ResponseEntity<ApiResponseData<List<String>>> getTeamMembers(
            @PathVariable Long archiveId) {
        // 팀원의 목록을 가져오는 서비스 호출 (teamEvaluationService가 아닌 다른 서비스일 수 있음)
        List<String> evaluators = teamEvaluationService.getEvaluators(archiveId);
        return ResponseEntity.ok(ApiResponseData.success(evaluators));
    }
} 
