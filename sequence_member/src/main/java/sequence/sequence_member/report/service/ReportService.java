package sequence.sequence_member.report.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.entity.EducationEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.project.entity.Comment;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.repository.CommentRepository;
import sequence.sequence_member.project.repository.ProjectRepository;
import sequence.sequence_member.report.dto.ReportRequestDTO;
import sequence.sequence_member.report.dto.ReportResponseDTO;
import sequence.sequence_member.report.dto.ReportTargetDTO;
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
    private final CommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final ArchiveRepository archiveRepository;
    private final JWTUtil jwtUtil;

    // 신고내용을 db에 저장
    public void submitReport(ReportRequestDTO reportRequestDTO, HttpServletRequest request) {
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

        // Refresh Token과 username이 일치하는지 확인
        String tokenUsername = jwtUtil.getUsername(refresh);
        if (!Objects.equals(tokenUsername, reportRequestDTO.getReporter())) {
            throw new CanNotFindResourceException("요청한 사용자와 로그인된 사용자가 다릅니다.");
        }

        boolean exist = memberRepository.existsByNickname(reportRequestDTO.getNickname());
        if (!exist) {
            throw new CanNotFindResourceException("해당 유저가 존재하지 않습니다.");
        }

        List<ReportResponseDTO> reportResponseDTOS = searchReport(reportRequestDTO.getNickname());
        for (ReportResponseDTO reportResponseDTO : reportResponseDTOS) {
            if (reportResponseDTO.getReporter().equals(reportRequestDTO.getReporter()) &&
                    reportResponseDTO.getReportTarget().equals(reportRequestDTO.getReportTarget())) {
                throw new CanNotFindResourceException("이미 신고되었습니다.");
            }
        }

        // reportTarget은 하나만 있어야 하므로, 리스트 첫 번째 값만 사용
        String targetType = String.valueOf(reportRequestDTO.getReportTarget());
        ReportEntity.ReportTarget reportTarget = ReportEntity.ReportTarget.valueOf(targetType);

        ReportEntity reportEntity = ReportEntity.builder()
                .nickname(reportRequestDTO.getNickname())
                .reporter(reportRequestDTO.getReporter())
                .reportTypes(reportRequestDTO.getReportType())
                .reportTarget(reportRequestDTO.getReportTarget())
                .reportContent(reportRequestDTO.getReportContent())
                .targetId(reportRequestDTO.getTargetId())
                .build();

        reportRepository.save(reportEntity);
    }

    // 신고 내역 조회
    public List<ReportResponseDTO> searchReport(String nickname) {
        List<ReportEntity> reportEntities = reportRepository.findByNickname(nickname);
        List<ReportResponseDTO> reportResponseDTOS = new ArrayList<>();

        for (ReportEntity reportEntity : reportEntities) {
            reportResponseDTOS.add(ReportResponseDTO.builder()
                    .id(reportEntity.getId())
                    .nickname(reportEntity.getNickname())
                    .reportTypes(reportEntity.getReportTypes().stream()
                            .map(ReportEntity.ReportType::getDescription)
                            .collect(Collectors.toList()))
                    .reportContent(reportEntity.getReportContent())
                    .reporter(reportEntity.getReporter())
                    .reportTarget(reportEntity.getReportTarget())
                    .targetId(reportEntity.getTargetId())
                    .build());
        }

        return reportResponseDTOS;
    }


    public ReportTargetDTO getReportTarget(Long userId, Long targetId, String targetType) {
        MemberEntity member = memberRepository.findById(userId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 유저가 존재하지 않습니다."));

        if(targetId == null){
            targetId = member.getId();
        };

        EducationEntity education = member.getEducation();
        if (education == null) {
            throw new CanNotFindResourceException("해당 유저의 학력 정보가 존재하지 않습니다.");
        }

        return new ReportTargetDTO(
                member.getNickname(),
                education.getSchoolName(),
                education.getMajor(),
                education.getGrade(),
                education.getDegree(),
                ReportEntity.ReportTarget.valueOf(targetType),
                targetId
        );
    }

    public ReportTargetDTO getDynamicReportTarget(String targetType, Long targetId) {
        switch (targetType) {
            case "USER":
                MemberEntity user = memberRepository.findById(targetId)
                        .orElseThrow(() -> new CanNotFindResourceException("해당 유저가 존재하지 않습니다."));
                return getReportTarget(user.getId(), null, "USER");

            case "COMMENT":
                Comment comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new CanNotFindResourceException("해당 댓글이 존재하지 않습니다."));
                return getReportTarget(comment.getWriter().getId(), comment.getId(), "COMMENT");

            case "PROJECT":
                Project project = projectRepository.findById(targetId)
                        .orElseThrow(() -> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));
                return getReportTarget(project.getWriter().getId(), project.getId(), "PROJECT");

            case "ARCHIVE":
                Archive archive = archiveRepository.findById(targetId)
                        .orElseThrow(() -> new CanNotFindResourceException("해당 아카이브가 존재하지 않습니다."));
                return getReportTarget(archive.getArchiveMembers().get(0).getId(), archive.getId(), "ARCHIVE");

            default:
                throw new IllegalArgumentException("지원하지 않는 신고 대상입니다: " + targetType);
        }
    }
}
