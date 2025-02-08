package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDto;
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDto;
import sequence.sequence_member.archive.entity.ArchiveMember;
import sequence.sequence_member.archive.entity.TeamEvaluation;
import sequence.sequence_member.archive.repository.TeamEvaluationRepository;
import sequence.sequence_member.global.enums.enums.Status;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.archive.repository.ArchiveMemberRepository;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.repository.ArchiveRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamEvaluationService {

    private final TeamEvaluationRepository teamEvaluationRepository;
    private final ArchiveMemberRepository archiveMemberRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TeamEvaluationResponseDto createTeamEvaluation(
            Long archiveId,
            String username,
            TeamEvaluationRequestDto requestDto) {
            
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }
            
        ArchiveMember evaluated = archiveMemberRepository.findById(requestDto.getEvaluatedMemberId())
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("피평가자 정보를 찾을 수 없습니다."));

        // 이미 평가했는지 확인
        if (teamEvaluationRepository.existsByEvaluatorAndEvaluated(evaluator, evaluated)) {
            return null;  // Controller에서 처리하도록 null 반환
        }

        // 팀 평가 엔티티 생성 및 검증
        TeamEvaluation teamEvaluation = TeamEvaluation.builder()
                .evaluator(evaluator)
                .evaluated(evaluated)
                .feedback(requestDto.getFeedback())
                .keyword(requestDto.getKeyword())
                .lineFeedback(requestDto.getLineFeedback())
                .build();

        if (!teamEvaluation.validateSameArchive()) {
            throw new BAD_REQUEST_EXCEPTION("같은 아카이브의 멤버만 평가할 수 있습니다.");
        }

        if (!teamEvaluation.validateSelfEvaluation()) {
            throw new BAD_REQUEST_EXCEPTION("자기 자신은 평가할 수 없습니다.");
        }

        // 저장 및 응답
        TeamEvaluation savedEvaluation = teamEvaluationRepository.save(teamEvaluation);
        return TeamEvaluationResponseDto.from(savedEvaluation);
    }

    // 아카이브의 모든 평가 완료 여부 확인 메소드 수정
    public boolean isAllEvaluationCompleted(Long archiveId, String username) {
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("아카이브 정보를 찾을 수 없습니다."));
        return teamEvaluationRepository.isAllEvaluationCompletedInArchive(archive);
    }

    public Map<String, Status> getEvaluationStatus(Long archiveId, String username) {
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        // 아카이브의 모든 멤버 조회
        List<ArchiveMember> archiveMembers = archiveMemberRepository.findAllByArchiveId(archiveId);
        Map<String, Status> statusMap = new HashMap<>();

        for (ArchiveMember targetMember : archiveMembers) {
            // 해당 멤버에 대한 모든 평가 수 조회
            long totalEvaluations = teamEvaluationRepository.countByEvaluated(targetMember);
            
            // 전체 팀원 수 - 1 (자기 자신 제외)
            long expectedEvaluations = archiveMembers.size() - 1;

            Status status;
            if (totalEvaluations == 0) {
                status = Status.평가전;
            } else if (totalEvaluations == expectedEvaluations) {
                status = Status.평가완료;
            } else {
                status = Status.평가중;
            }

            // 멤버의 닉네임을 key로 사용 (username 대신 nickname 사용)
            statusMap.put(targetMember.getMember().getNickname(), status);
        }

        return statusMap;
    }
} 