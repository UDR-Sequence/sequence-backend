package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDto;
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
import sequence.sequence_member.global.exception.ArchiveNotFoundException;
import java.util.stream.Collectors;
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamEvaluationService {

    private final TeamEvaluationRepository teamEvaluationRepository;
    private final ArchiveMemberRepository archiveMemberRepository;
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createTeamEvaluation(
            Long archiveId,
            String username,
            TeamEvaluationRequestDto requestDto) {
            
        // archive 존재 여부 먼저 확인
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new ArchiveNotFoundException("아카이브 정보를 찾을 수 없습니다."));
            
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        for (TeamEvaluationRequestDto.EvaluationItem evaluation : requestDto.getEvaluations()) {
            // 피평가자 찾기
            MemberEntity evaluatedMember = memberRepository.findByUsername(evaluation.getEvaluatedUserName())
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("피평가자를 찾을 수 없습니다: " + evaluation.getEvaluatedUserName()));
            
            ArchiveMember evaluated = archiveMemberRepository.findByMemberAndArchive_Id(evaluatedMember, archiveId);
            if (evaluated == null) {
                throw new BAD_REQUEST_EXCEPTION("피평가자가 해당 아카이브의 멤버가 아닙니다: " + evaluation.getEvaluatedUserName());
            }

            // 이미 평가했는지 확인
            if (teamEvaluationRepository.existsByEvaluatorAndEvaluated(evaluator, evaluated)) {
                throw new BAD_REQUEST_EXCEPTION("이미 평가를 완료했습니다: " + evaluation.getEvaluatedUserName());
            }

            // 키워드 리스트를 JSON 배열 형식으로 변환
            String keywordJson = "[\"" + String.join("\",\"", evaluation.getKeyword()) + "\"]";

            // 팀 평가 엔티티 생성 및 검증
            TeamEvaluation teamEvaluation = TeamEvaluation.builder()
                    .evaluator(evaluator)
                    .evaluated(evaluated)
                    .feedback(evaluation.getFeedback())
                    .keyword(keywordJson)
                    .build();

            if (!teamEvaluation.validateSameArchive()) {
                throw new BAD_REQUEST_EXCEPTION("같은 아카이브의 멤버만 평가할 수 있습니다: " + evaluation.getEvaluatedUserName());
            }

            if (!teamEvaluation.validateSelfEvaluation()) {
                throw new BAD_REQUEST_EXCEPTION("자기 자신은 평가할 수 없습니다: " + evaluation.getEvaluatedUserName());
            }

            teamEvaluationRepository.save(teamEvaluation);
        }
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

        // 각 팀원별로 평가 상태 확인
        for (ArchiveMember archiveMember : archiveMembers) {
            int totalMembersToEvaluate = archiveMembers.size() - 1; // 자신 제외
            int evaluatedCount = 0;
            
            // 해당 팀원이 다른 팀원들을 평가했는지 확인
            for (ArchiveMember targetMember : archiveMembers) {
                if (archiveMember.equals(targetMember)) {
                    continue;  // 자기 자신은 건너뛰기
                }

                boolean hasEvaluated = teamEvaluationRepository.existsByEvaluatorAndEvaluated(archiveMember, targetMember);
                if (hasEvaluated) {
                    evaluatedCount++;
                }
            }

            // 해당 팀원이 모든 팀원을 평가했는지 확인
            Status memberStatus = (evaluatedCount == totalMembersToEvaluate) ? Status.평가완료 : Status.평가전;
            statusMap.put(archiveMember.getMember().getNickname(), memberStatus);
        }

        return statusMap;
    }

    public List<String> getEvaluators(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("아카이브 정보를 찾을 수 없습니다."));

        // 해당 아카이브에서 평가를 진행한 평가자들의 목록 조회
        List<ArchiveMember> evaluators = teamEvaluationRepository.findDistinctEvaluatorsByArchive(archive);

        // 평가자들의 사용자명 리스트로 변환하여 반환
        return evaluators.stream()
                .map(evaluator -> evaluator.getMember().getUsername())
                .toList();
    }

    public List<TeamEvaluationResponseDto> getTeamEvaluations(Long archiveId, String username) {
        // archive 존재 여부 확인
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new ArchiveNotFoundException("아카이브 정보를 찾을 수 없습니다."));
        
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        // 아카이브의 모든 멤버 조회
        List<ArchiveMember> allMembers = archiveMemberRepository.findAllByArchiveId(archiveId);
        
        // 평가 대상 목록 생성 (자신 제외)
        List<TeamEvaluationResponseDto.EvaluatedInfo> evaluatedList = allMembers.stream()
            .filter(archiveMember -> !archiveMember.getId().equals(evaluator.getId()))
            .map(evaluated -> TeamEvaluationResponseDto.EvaluatedInfo.builder()
                .username(evaluated.getMember().getUsername())
                .role(evaluated.getRole())
                .profileImg(evaluated.getMember().getProfileImg())
                .build())
            .collect(Collectors.toList());

        return List.of(TeamEvaluationResponseDto.builder()
            .evaluator(TeamEvaluationResponseDto.EvaluatorInfo.builder()
                .username(evaluator.getMember().getUsername())
                .role(evaluator.getRole())
                .profileImg(evaluator.getMember().getProfileImg())
                .build())
            .evaluated(evaluatedList)
            .build());
    }
} 
