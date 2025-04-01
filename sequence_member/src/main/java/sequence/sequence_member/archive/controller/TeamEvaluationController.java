package sequence.sequence_member.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDTO;
import sequence.sequence_member.archive.service.TeamEvaluationService;
import sequence.sequence_member.global.response.ApiResponseData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDTO;
import sequence.sequence_member.archive.dto.TeamEvaluationStatusResponseDTO;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class TeamEvaluationController {

    private final TeamEvaluationService teamEvaluationService;

    @PostMapping("/{archiveId}/evaluations")
    public ResponseEntity<ApiResponseData<Void>> createTeamEvaluation(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TeamEvaluationRequestDTO requestDto) {
            
        teamEvaluationService.createTeamEvaluation(archiveId, userDetails.getUsername(), requestDto);
        return ResponseEntity
                .status(Code.CREATED.getStatus())
                .body(ApiResponseData.of(Code.CREATED.getCode(), "팀원 평가가 완료되었습니다.", null));
    }

    @GetMapping("/{archiveId}/evaluations")
    public ResponseEntity<ApiResponseData<List<TeamEvaluationResponseDTO>>> getTeamEvaluations(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
    
        List<TeamEvaluationResponseDTO> evaluations = teamEvaluationService.getTeamEvaluations(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(evaluations));
    }

    @GetMapping("/{archiveId}/evaluations/status")
    public ResponseEntity<ApiResponseData<TeamEvaluationStatusResponseDTO>> getEvaluationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long archiveId) {
        
        // 아카이브 상태를 체크하고 필요시 업데이트
        teamEvaluationService.checkAndUpdateEvaluationStatus(archiveId);
        
        // 팀원별 평가 상태 조회
        TeamEvaluationStatusResponseDTO statusResponse =
            teamEvaluationService.getEvaluationStatus(archiveId, userDetails.getUsername());
        
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            statusResponse.isAllCompleted() ? 
                "모든 팀원의 평가가 완료되었습니다." : 
                "팀원 평가 상태를 조회했습니다.",
            statusResponse
        ));
    }

    @GetMapping("/{archiveId}/team-members")
    public ResponseEntity<ApiResponseData<List<String>>> getTeamMembers(
            @PathVariable Long archiveId) {
        // 팀원의 목록을 가져오는 서비스 호출 (teamEvaluationService가 아닌 다른 서비스일 수 있음)
        List<String> evaluators = teamEvaluationService.getEvaluators(archiveId);
        return ResponseEntity.ok(ApiResponseData.success(evaluators));
    }

    @GetMapping("/{archiveId}/evaluations/complete-check")
    public ResponseEntity<ApiResponseData<Boolean>> checkAndUpdateEvaluationStatus(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        boolean isCompleted = teamEvaluationService.checkAndUpdateEvaluationStatus(archiveId);
        
        String message = isCompleted ? 
                "모든 팀원의 평가가 완료되었습니다. 아카이브 상태가 '평가완료'로 변경되었습니다." : 
                "아직 모든 팀원의 평가가 완료되지 않았습니다.";
        
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            message,
            isCompleted
        ));
    }
} 
