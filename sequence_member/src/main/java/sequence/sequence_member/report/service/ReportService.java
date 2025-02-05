package sequence.sequence_member.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.report.dto.ReportRequestDTO;
import sequence.sequence_member.report.dto.ReportResponseDTO;
import sequence.sequence_member.report.entity.ReportEntity;
import sequence.sequence_member.report.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    //신고내용을 db에 저장
    public void submitReport(ReportRequestDTO reportRequestDTO){
        boolean exist = memberRepository.existsByNickname(reportRequestDTO.getNickname());
        if(!exist){
            throw new CanNotFindResourceException("해당 유저가 존재하지 않습니다.");
        }

        ReportEntity reportEntity = ReportEntity.builder()
                        .nickname(reportRequestDTO.getNickname())
                        .reporter(reportRequestDTO.getReporter())
                        .reportType(reportRequestDTO.getReportType())
                        .reportContent(reportRequestDTO.getReportContent())
                        .build();

        reportRepository.save(reportEntity);
    }

    //신고 내역 조회
    public List<ReportResponseDTO> searchReport(String nickname){
        List<ReportEntity> reportEntities = reportRepository.findByNickname(nickname);
        List<ReportResponseDTO> reportResponseDTOS = new ArrayList<>();

        for(ReportEntity reportEntity : reportEntities){
            reportResponseDTOS.add(ReportResponseDTO.builder()
                    .reportType(reportEntity.getReportType())
                    .reportContent(reportEntity.getReportContent())
                    .reporter(reportEntity.getReporter())
                    .build());
        }

        return reportResponseDTOS;
    }

}
