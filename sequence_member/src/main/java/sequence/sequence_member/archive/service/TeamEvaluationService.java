package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.archive.dto.TeamEvaluationStatusResponseDTO;
import sequence.sequence_member.archive.dto.TeamEvaluationResponseDTO;
import sequence.sequence_member.archive.dto.TeamEvaluationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;


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
            TeamEvaluationRequestDTO requestDto) {
            
        // archive 존재 여부 먼저 확인 (삭제되지 않은 것만)
        Archive archive = archiveRepository.findByIdAndIsDeletedFalse(archiveId)
            .orElseThrow(() -> new ArchiveNotFoundException("아카이브 정보를 찾을 수 없습니다."));
            
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        for (TeamEvaluationRequestDTO.EvaluationItem evaluation : requestDto.getEvaluations()) {
            // 피평가자 찾기 - nickname으로 변경
            MemberEntity evaluatedMember = memberRepository.findByNickname(evaluation.getEvaluatedNickname())
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("피평가자를 찾을 수 없습니다: " + evaluation.getEvaluatedNickname()));
            
            ArchiveMember evaluated = archiveMemberRepository.findByMemberAndArchive_Id(evaluatedMember, archiveId);
            if (evaluated == null) {
                throw new BAD_REQUEST_EXCEPTION("피평가자가 해당 아카이브의 멤버가 아닙니다: " + evaluation.getEvaluatedNickname());
            }

            // 이미 평가했는지 확인 (삭제되지 않은 평가만)
            if (teamEvaluationRepository.existsByEvaluatorAndEvaluatedAndIsDeletedFalse(evaluator, evaluated)) {
                throw new BAD_REQUEST_EXCEPTION("이미 평가를 완료했습니다: " + evaluation.getEvaluatedNickname());
            }

            // 키워드 리스트를 Map으로 변환 (각 키워드의 개수를 1로 설정)
            Map<String, Integer> keywordMap = new HashMap<>();
            for (String keyword : evaluation.getKeyword()) {
                keywordMap.merge(keyword, 1, Integer::sum);
            }
            
            // Map을 JSON 문자열로 변환
            ObjectMapper mapper = new ObjectMapper();
            String keywordJson;
            try {
                keywordJson = mapper.writeValueAsString(keywordMap);
            } catch (JsonProcessingException e) {
                throw new BAD_REQUEST_EXCEPTION("키워드 변환 중 오류가 발생했습니다.");
            }

            // 팀 평가 엔티티 생성 및 검증
            TeamEvaluation teamEvaluation = TeamEvaluation.builder()
                    .evaluator(evaluator)
                    .evaluated(evaluated)
                    .feedback(evaluation.getFeedback())
                    .keyword(keywordJson)
                    .build();

            if (!teamEvaluation.validateSameArchive()) {
                throw new BAD_REQUEST_EXCEPTION("같은 아카이브의 멤버만 평가할 수 있습니다: " + evaluation.getEvaluatedNickname());
            }

            if (!teamEvaluation.validateSelfEvaluation()) {
                throw new BAD_REQUEST_EXCEPTION("자기 자신은 평가할 수 없습니다: " + evaluation.getEvaluatedNickname());
            }

            teamEvaluationRepository.save(teamEvaluation);
        }

        // 모든 팀원 평가가 완료되었는지 확인 (삭제되지 않은 평가만)
        boolean isAllCompleted = teamEvaluationRepository.isAllEvaluationCompletedInArchiveAndNotDeleted(archive);
        
        // 모든 평가가 완료되면 아카이브 상태 변경
        if (isAllCompleted && archive.getStatus() == Status.평가전) {
            archive.setStatus(Status.평가완료);
            archiveRepository.save(archive);
        }
    }

    // 아카이브의 모든 평가 완료 여부 확인 메소드 수정
    public boolean isAllEvaluationCompleted(Long archiveId, String username) {
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember archiveMember = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (archiveMember == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        Archive archive = archiveRepository.findByIdAndIsDeletedFalse(archiveId)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("아카이브 정보를 찾을 수 없습니다."));
        return teamEvaluationRepository.isAllEvaluationCompletedInArchiveAndNotDeleted(archive);
    }

    public TeamEvaluationStatusResponseDTO getEvaluationStatus(Long archiveId, String username) {
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        // 아카이브의 모든 멤버 조회
        List<ArchiveMember> archiveMembers = archiveMemberRepository.findAllByArchiveId(archiveId);
        Map<String, TeamEvaluationStatusResponseDTO.MemberEvaluationStatus> statusMap = new HashMap<>();

        // 각 팀원별로 평가 상태 확인
        for (ArchiveMember archiveMember : archiveMembers) {
            Status memberStatus = getArvhiceMemberEvaluationStatus(archiveMember, archiveMembers);

            // 역할 정보 가져오기
            List<ProjectRole> roles = new ArrayList<>();
            if (archiveMember.getMember().getEducation() != null) {
                roles = archiveMember.getMember().getEducation().getDesiredJob();
            }

            // MemberEvaluationStatus 생성
            TeamEvaluationStatusResponseDTO.MemberEvaluationStatus status = 
                TeamEvaluationStatusResponseDTO.MemberEvaluationStatus.builder()
                    .nickname(archiveMember.getMember().getNickname())
                    .roles(roles)
                    .status(memberStatus)
                    .build();

            statusMap.put(archiveMember.getMember().getNickname(), status);
        }

        // 모든 멤버가 평가를 완료했는지 확인
        boolean isAllCompleted = statusMap.values().stream()
            .allMatch(status -> status.getStatus() == Status.평가완료);

        return TeamEvaluationStatusResponseDTO.builder()
            .memberStatus(statusMap)
            .isAllCompleted(isAllCompleted)
            .build();
    }

    @NotNull
    public Status getArvhiceMemberEvaluationStatus(ArchiveMember archiveMember, List<ArchiveMember> archiveMembers) {
        int totalMembersToEvaluate = archiveMembers.size() - 1; // 자신 제외
        int evaluatedCount = 0;

        // 해당 팀원이 다른 팀원들을 평가했는지 확인 (삭제되지 않은 평가만)
        for (ArchiveMember targetMember : archiveMembers) {
            if (archiveMember.equals(targetMember)) {
                continue;  // 자기 자신은 건너뛰기
            }

            boolean hasEvaluated = teamEvaluationRepository.existsByEvaluatorAndEvaluatedAndIsDeletedFalse(
                    archiveMember,
                targetMember
            );
            if (hasEvaluated) {
                evaluatedCount++;
            }
        }

        // 해당 팀원이 모든 팀원을 평가했는지 확인
        Status memberStatus = (evaluatedCount == totalMembersToEvaluate) ?
            Status.평가완료 : Status.평가전;
        return memberStatus;
    }

    public List<String> getEvaluators(Long archiveId) {
        Archive archive = archiveRepository.findByIdAndIsDeletedFalse(archiveId)
                .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("아카이브 정보를 찾을 수 없습니다."));

        // 해당 아카이브에서 평가를 진행한 평가자들의 목록 조회 (삭제되지 않은 평가만)
        List<ArchiveMember> evaluators = teamEvaluationRepository.findDistinctEvaluatorsByArchiveAndNotDeleted(archive);

        // 평가자들의 닉네임 리스트로 변환하여 반환
        return evaluators.stream()
                .map(evaluator -> evaluator.getMember().getNickname())
                .toList();
    }

    public List<TeamEvaluationResponseDTO> getTeamEvaluations(Long archiveId, String username) {
        // archive 존재 여부 확인 (삭제되지 않은 것만)
        Archive archive = archiveRepository.findByIdAndIsDeletedFalse(archiveId)
            .orElseThrow(() -> new ArchiveNotFoundException("아카이브 정보를 찾을 수 없습니다."));
        
        // username으로 멤버 찾기
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new BAD_REQUEST_EXCEPTION("사용자를 찾을 수 없습니다."));
            
        // 평가자의 ArchiveMember 정보 조회
        ArchiveMember evaluator = archiveMemberRepository.findByMemberAndArchive_Id(member, archiveId);
        if (evaluator == null) {
            throw new BAD_REQUEST_EXCEPTION("해당 아카이브의 멤버가 아닙니다.");
        }

        // 평가자의 역할 정보 가져오기
        List<ProjectRole> evaluatorRoles = new ArrayList<>();
        if (member.getEducation() != null && member.getEducation().getDesiredJob() != null) {
            evaluatorRoles = member.getEducation().getDesiredJob();
        }

        // 아카이브의 모든 멤버 조회
        List<ArchiveMember> allMembers = archiveMemberRepository.findAllByArchiveId(archiveId);
        
        // 평가 대상 목록 생성 (자신 제외)
        List<TeamEvaluationResponseDTO.EvaluatedInfo> evaluatedList = allMembers.stream()
            .filter(archiveMember -> !archiveMember.getId().equals(evaluator.getId()))
            .map(evaluated -> {
                // 각 멤버의 역할 정보 가져오기
                List<ProjectRole> roles = new ArrayList<>();
                MemberEntity evaluatedMember = evaluated.getMember();
                if (evaluatedMember.getEducation() != null && evaluatedMember.getEducation().getDesiredJob() != null) {
                    roles = evaluatedMember.getEducation().getDesiredJob();
                }
                
                return TeamEvaluationResponseDTO.EvaluatedInfo.builder()
                    .nickname(evaluatedMember.getNickname())
                    .profileImg(evaluatedMember.getProfileImg())
                    .roles(roles)  // 역할 정보 추가
                    .build();
            })
            .collect(Collectors.toList());

        return List.of(TeamEvaluationResponseDTO.builder()
            .evaluator(TeamEvaluationResponseDTO.EvaluatorInfo.builder()
                .nickname(member.getNickname())
                .profileImg(member.getProfileImg())
                .roles(evaluatorRoles)  // 역할 정보 추가
                .build())
            .evaluated(evaluatedList)
            .startDate(archive.getStartDate())
            .endDate(archive.getEndDate())
            .build());
    }

    @Transactional
    public boolean checkAndUpdateEvaluationStatus(Long archiveId) {
        Archive archive = archiveRepository.findByIdAndIsDeletedFalse(archiveId)
            .orElseThrow(() -> new ArchiveNotFoundException("아카이브 정보를 찾을 수 없습니다."));
        
        boolean isAllCompleted = teamEvaluationRepository.isAllEvaluationCompletedInArchiveAndNotDeleted(archive);
        
        // 모든 평가가 완료되었고, 현재 상태가 평가전이면 평가완료로 변경
        if (isAllCompleted && archive.getStatus() == Status.평가전) {
            archive.setStatus(Status.평가완료);
            archiveRepository.save(archive);
        }
        
        return isAllCompleted;
    }
} 
