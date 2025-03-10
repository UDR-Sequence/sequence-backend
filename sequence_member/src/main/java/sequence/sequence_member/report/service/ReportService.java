package sequence.sequence_member.report.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.report.dto.ReportRequestDTO;
import sequence.sequence_member.report.dto.ReportResponseDTO;
import sequence.sequence_member.report.entity.ReportEntity;
import sequence.sequence_member.report.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    //신고내용을 db에 저장
    public void submitReport(ReportRequestDTO reportRequestDTO, HttpServletRequest request){
        // 쿠키 확인
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CanNotFindResourceException("로그인이 필요합니다.");
        }

        // Refresh 토큰 찾기
        String refresh = null;
        for (Cookie cookie : cookies) {
            if ("refresh".equals(cookie.getName())) {
                refresh = cookie.getValue();
            }
        }

        //Refresh Token과 username이 일치하는지 확인
        String tokenUsername = jwtUtil.getUsername(refresh);
        if (!Objects.equals(tokenUsername, reportRequestDTO.getReporter())) {
            throw new CanNotFindResourceException("요청한 사용자와 로그인된 사용자가 다릅니다.");
        }

        boolean exist = memberRepository.existsByNickname(reportRequestDTO.getNickname());
        if(!exist){
            throw new CanNotFindResourceException("해당 유저가 존재하지 않습니다.");
        }

        List<ReportResponseDTO> reportResponseDTOS = searchReport(reportRequestDTO.getNickname());
        for (ReportResponseDTO reportResponseDTO : reportResponseDTOS) {
            if(reportResponseDTO.getReporter().equals(reportRequestDTO.getReporter())){
                throw new CanNotFindResourceException("이미 신고된 유저 입니다.");
            }
        }

        ReportEntity reportEntity = ReportEntity.builder()
                        .nickname(reportRequestDTO.getNickname())
                        .reporter(reportRequestDTO.getReporter())
                        .reportTypes(reportRequestDTO.getReportType())
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
                    .reportTypes(reportEntity.getReportTypes().stream()
                            .map(ReportEntity.ReportType::getDescription)
                            .collect(Collectors.toList()))
                    .reportContent(reportEntity.getReportContent())
                    .reporter(reportEntity.getReporter())
                    .build());
        }

        return reportResponseDTOS;
    }
}
