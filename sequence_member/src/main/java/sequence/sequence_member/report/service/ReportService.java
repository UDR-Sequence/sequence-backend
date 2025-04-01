package sequence.sequence_member.report.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
            if(reportResponseDTO.getReporter().equals(reportRequestDTO.getReporter()) && reportResponseDTO.getReportTarget().equals(reportRequestDTO.getReportTarget())){
                throw new CanNotFindResourceException("이미 신고되었습니다.");
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
                    .id(reportEntity.getId())
                    .nickname(reportEntity.getNickname())
                    .reportTypes(reportEntity.getReportTypes().stream()
                            .map(ReportEntity.ReportType::getDescription)
                            .collect(Collectors.toList()))
                    .reportContent(reportEntity.getReportContent())
                    .reporter(reportEntity.getReporter())
                    .build());
        }

        return reportResponseDTOS;
    }

    public ReportTargetDTO getReportTarget(String nickname, Long postId, String targetType) {
        MemberEntity member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new CanNotFindResourceException("해당 유저가 존재하지 않습니다."));

        Long targetId = postId;
        if(postId == null){
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
                List.of(targetType == null ? "USER" : targetType),
                targetId
        );
    }

    public ReportTargetDTO getReportCommentTarget(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 댓글이 존재하지 않습니다."));

        return getReportTarget(String.valueOf(comment.getWriter()), commentId, "COMMENT");
    }

    public ReportTargetDTO getReportProjectTarget(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));

        return getReportTarget(String.valueOf(project.getWriter()), projectId, "PROJECT");
    }

    public ReportTargetDTO getReportArchiveTarget(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new CanNotFindResourceException("해당 프로젝트가 존재하지 않습니다."));

        return getReportTarget(String.valueOf(archive.getArchiveMembers().get(0)), archiveId, "ARCHIVE");
    }

}
