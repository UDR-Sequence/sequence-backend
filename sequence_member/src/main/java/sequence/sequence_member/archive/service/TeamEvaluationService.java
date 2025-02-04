package sequence.sequence_member.archive.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.archive.dto.TeamEvaluationDTO;
import sequence.sequence_member.archive.entity.TeamEvaluationEntity;
import sequence.sequence_member.archive.repository.TeamEvaluationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamEvaluationService {

    private final TeamEvaluationRepository teamEvaluationRepository;

    // 🔹 평가 저장
    @Transactional
    public TeamEvaluationDTO saveEvaluation(TeamEvaluationDTO evaluationDTO) {
        TeamEvaluationEntity evaluation = convertToEntity(evaluationDTO);
        TeamEvaluationEntity savedEvaluation = teamEvaluationRepository.save(evaluation);
        return convertToDto(savedEvaluation);
    }

    // 🔹 특정 평가자가 남긴 평가 조회
    public List<TeamEvaluationDTO> getEvaluationsByEvaluator(Long evaluatorId) {
        List<TeamEvaluationEntity> evaluations = teamEvaluationRepository.findByEvaluatorId_MemberId(evaluatorId);
        return evaluations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 특정 피평가자에 대한 평가 조회
    public List<TeamEvaluationDTO> getEvaluationsByEvaluatee(Long evaluateeId) {
        List<TeamEvaluationEntity> evaluations = teamEvaluationRepository.findByEvaluateeId_MemberId(evaluateeId);
        return evaluations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 DTO → Entity 변환
    private TeamEvaluationEntity convertToEntity(TeamEvaluationDTO dto) {
        return TeamEvaluationEntity.builder()
                .evaluationId(dto.getEvaluationId())
                .evaluatorId(dto.getEvaluatorId().toEntity()) // MemberDTO → MemberEntity 변환 필요
                .evaluateeId(dto.getEvaluateeId().toEntity()) // MemberDTO → MemberEntity 변환 필요
                .feedback(dto.getFeedback())
                .keyword(dto.getKeyword())
                .lineFeedback(dto.getLineFeedback())
                .evaluationDate(dto.getEvaluationDate())
                .build();
    }

    // 🔹 Entity → DTO 변환
    private TeamEvaluationDTO convertToDto(TeamEvaluationEntity entity) {
        return TeamEvaluationDTO.builder()
                .evaluationId(entity.getEvaluationId())
                .evaluatorId(entity.getEvaluatorId().toDto()) // MemberEntity → MemberDTO 변환 필요
                .evaluateeId(entity.getEvaluateeId().toDto()) // MemberEntity → MemberDTO 변환 필요
                .feedback(entity.getFeedback())
                .keyword(entity.getKeyword())
                .lineFeedback(entity.getLineFeedback())
                .evaluationDate(entity.getEvaluationDate())
                .build();
    }
}
