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
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class TeamEvaluationController {

    private final TeamEvaluationService teamEvaluationService;

    @PostMapping("/{archiveId}/evaluations")
    public ResponseEntity<ApiResponseData<Void>> createTeamEvaluation(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TeamEvaluationRequestDto requestDto) {
            
        teamEvaluationService.createTeamEvaluation(archiveId, userDetails.getUsername(), requestDto);
        return ResponseEntity
                .status(Code.CREATED.getStatus())
                .body(ApiResponseData.of(Code.CREATED.getCode(), "팀원 평가가 완료되었습니다.", null));
    }

    @GetMapping("/{archiveId}/evaluations")
    public ResponseEntity<ApiResponseData<List<TeamEvaluationResponseDto>>> getTeamEvaluations(
            @PathVariable Long archiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
    
        List<TeamEvaluationResponseDto> evaluations = teamEvaluationService.getTeamEvaluations(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(evaluations));
    }

    @GetMapping("/{archiveId}/evaluations/status")
    public ResponseEntity<ApiResponseData<Map<String, Object>>> getEvaluationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long archiveId) {
        
        // 아카이브 상태를 체크하고 필요시 업데이트
        boolean isAllCompleted = teamEvaluationService.checkAndUpdateEvaluationStatus(archiveId);
        
        // 기존 팀원별 평가 상태 조회
        Map<String, Status> memberEvaluationStatus = teamEvaluationService.getEvaluationStatus(archiveId, userDetails.getUsername());
        
        // 응답 데이터 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("memberStatus", memberEvaluationStatus); // 팀원별 평가 상태
        responseData.put("isAllCompleted", isAllCompleted); // 전체 평가 완료 여부
        
        return ResponseEntity.ok(ApiResponseData.of(
            Code.SUCCESS.getCode(),
            isAllCompleted ? "모든 팀원의 평가가 완료되었습니다." : "팀원 평가 상태를 조회했습니다.",
            responseData
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
