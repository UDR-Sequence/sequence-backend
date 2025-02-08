package sequence.sequence_member.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDto;
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDto;
import sequence.sequence_member.archive.service.TeamEvaluationService;
import sequence.sequence_member.global.response.ApiResponseData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import sequence.sequence_member.member.dto.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.global.enums.enums.Status;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class TeamEvaluationController {

    private final TeamEvaluationService teamEvaluationService;

    @PostMapping("/{archiveId}/evaluations")
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
} 