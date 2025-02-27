package sequence.sequence_member.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDto;
<<<<<<< HEAD
import sequence.sequence_member.archive.entity.TeamEvaluation;
=======
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDto;
>>>>>>> a02a9c7381dd303feb43d231b5be08815f0d712e
import sequence.sequence_member.archive.service.TeamEvaluationService;
import sequence.sequence_member.global.response.ApiResponseData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import sequence.sequence_member.member.dto.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.global.enums.enums.Status;
<<<<<<< HEAD
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDto;
=======
>>>>>>> a02a9c7381dd303feb43d231b5be08815f0d712e

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
<<<<<<< HEAD
import java.util.stream.Collectors;
=======
>>>>>>> a02a9c7381dd303feb43d231b5be08815f0d712e

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class TeamEvaluationController {

    private final TeamEvaluationService teamEvaluationService;

    @PostMapping("/{archiveId}/evaluations")
<<<<<<< HEAD
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
    
        List<TeamEvaluationResponseDto> responseDtos = teamEvaluationService.getTeamEvaluations(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(responseDtos));
=======
    public ResponseEntity<ApiResponseData<?>> createTeamEvaluation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long archiveId,
            @RequestBody @Valid TeamEvaluationRequestDto requestDto) {
            
        System.out.println("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("UserDetails: " + userDetails);
        
        TeamEvaluationResponseDto responseDto = teamEvaluationService.createTeamEvaluation(
            archiveId,
            userDetails.getUsername(),
            requestDto
        );
        
        // 이미 평가가 존재하는 경우
        if (responseDto == null) {
            return ResponseEntity
                    .status(Code.ALREADY_EXISTS.getStatus())
                    .body(ApiResponseData.of(Code.ALREADY_EXISTS.getCode(), "이미 평가한 팀원입니다.", null));
        }
        
        // 새로운 평가인 경우
        return ResponseEntity
                .status(Code.CREATED.getStatus())
                .body(ApiResponseData.of(Code.CREATED.getCode(), "팀원 평가가 성공적으로 저장되었습니다.", responseDto));
>>>>>>> a02a9c7381dd303feb43d231b5be08815f0d712e
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
