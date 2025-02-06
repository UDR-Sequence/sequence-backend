package sequence.sequence_member.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.service.MemberService;
import sequence.sequence_member.report.dto.ReportRequestDTO;
import sequence.sequence_member.report.dto.ReportResponseDTO;
import sequence.sequence_member.report.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final MemberService memberService;

    //신고하기
    @PostMapping("/submit")
    public ResponseEntity<ApiResponseData<String>> submitReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        //신고내역 저장
        reportService.submitReport(reportRequestDTO);

        //신고완료
        return ResponseEntity.ok().body(ApiResponseData.success(null,"신고가 완료되었습니다."));
    }

    //신고내역 조회
    @GetMapping("/{nickname}")
    public ResponseEntity<ApiResponseData<List<ReportResponseDTO>>> getReport(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "신고내역 조회가 성공했습니다.",reportService.searchReport(nickname)));
    }

}
