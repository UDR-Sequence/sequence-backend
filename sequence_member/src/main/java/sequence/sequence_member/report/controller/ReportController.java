package sequence.sequence_member.report.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
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

    // 신고 대상 정보 조회
    @GetMapping("/target/user/{nickname}")
    public ResponseEntity<ApiResponseData<ReportTargetDTO>> getReportTarget(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok().body(ApiResponseData.success(reportService.getReportTarget(nickname, null, null), "신고 대상 정보 조회 성공"));
    }

    // 신고 대상 댓글 조회
    @GetMapping("/target/comment/{commentId}")
    public ResponseEntity<ApiResponseData<ReportTargetDTO>> getReportCommentTarget(@PathVariable("commentId") Long commentId) {

        return ResponseEntity.ok().body(ApiResponseData.success(reportService.getReportCommentTarget(commentId), "신고 대상 정보 조회 성공"));
    }

    // 신고 대상 프로젝트 조회
    @GetMapping("/target/project/{projectId}")
    public ResponseEntity<ApiResponseData<ReportTargetDTO>> getReportProjectTarget(@PathVariable("projectId") Long projectId) {

        return ResponseEntity.ok().body(ApiResponseData.success(reportService.getReportProjectTarget(projectId), "신고 대상 정보 조회 성공"));
    }

    // 신고 대상 프로젝트 조회
    @GetMapping("/target/archive/{archiveId}")
    public ResponseEntity<ApiResponseData<ReportTargetDTO>> getReportArchiveTarget(@PathVariable("archiveId") Long archiveId) {

        return ResponseEntity.ok().body(ApiResponseData.success(reportService.getReportArchiveTarget(archiveId), "신고 대상 정보 조회 성공"));
    }
}
