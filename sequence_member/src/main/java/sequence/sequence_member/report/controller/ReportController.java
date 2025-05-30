package sequence.sequence_member.report.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.report.dto.ReportRequestDTO;
import sequence.sequence_member.report.dto.ReportResponseDTO;
import sequence.sequence_member.report.dto.ReportTargetDTO;
import sequence.sequence_member.report.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    //신고하기
    @PostMapping("/submit")
    public ResponseEntity<ApiResponseData<String>> submitReport(@RequestBody ReportRequestDTO reportRequestDTO, HttpServletRequest request) {
        //신고내역 저장
        reportService.submitReport(reportRequestDTO, request);

        //신고완료
        return ResponseEntity.ok().body(ApiResponseData.success(null,"신고가 완료되었습니다."));
    }

    //신고내역 조회
    @GetMapping("/{nickname}")
    public ResponseEntity<ApiResponseData<List<ReportResponseDTO>>> getReport(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "신고내역 조회가 성공했습니다.",reportService.searchReport(nickname)));
    }

    @GetMapping("/target/{targetType}/{targetId}")
    public ResponseEntity<ApiResponseData<ReportTargetDTO>> getReportTarget(
            @PathVariable("targetType") String targetType,
            @PathVariable("targetId") Long targetId
    ) {
        ReportTargetDTO targetDTO = reportService.getDynamicReportTarget(targetType.toUpperCase(), targetId);
        return ResponseEntity.ok().body(ApiResponseData.success(targetDTO, "신고 대상 정보 조회 성공"));
    }

    // 신고 내역 철회 (Soft Delete)
    @PostMapping("/delete/{reportId}")
    public ResponseEntity<ApiResponseData<String>> cancelReport(
            @PathVariable("reportId") Long reportId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        reportService.deleteReport(reportId, customUserDetails);
        return ResponseEntity.ok().body(ApiResponseData.success(null, "신고가 철회, 삭제되었습니다."));
    }

}
