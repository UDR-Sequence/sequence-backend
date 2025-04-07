package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.archive.dto.FeedbackDetailDTO;
import sequence.sequence_member.archive.dto.KeywordStatDTO;
import sequence.sequence_member.archive.dto.MyPageEvaluationDTO;
import sequence.sequence_member.archive.entity.TeamEvaluation;
import sequence.sequence_member.archive.repository.TeamEvaluationRepository;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.global.enums.enums.Period;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageEvaluationService {
    private final TeamEvaluationRepository teamEvaluationRepository;
    private final MemberRepository memberRepository;

    public MyPageEvaluationDTO getMyEvaluation(String nickname) {
        MemberEntity member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 사용자를 찾을 수 없습니다: " + nickname));
        
        List<TeamEvaluation> evaluations = teamEvaluationRepository.findByEvaluatedId(member.getId());
        
        // 키워드 통계 수집
        Map<String, Integer> keywordStats = new HashMap<>();
        evaluations.stream()
                .map(TeamEvaluation::getKeywordMap)
                .forEach(map -> map.forEach((key, value) -> 
                    keywordStats.merge(key, value, Integer::sum)));
        
        // 키워드를 count 내림차순으로 정렬
        List<KeywordStatDTO> keywordStatDTOs = keywordStats.entrySet().stream()
                .map(entry -> new KeywordStatDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(KeywordStatDTO::getCount).reversed())
                .collect(Collectors.toList());

        // 피드백 수집 및 기간 오름차순으로 정렬
        List<FeedbackDetailDTO> feedbackDTOs = evaluations.stream()
                .map(TeamEvaluation::toFeedbackDetailDTO)
                .sorted(Comparator.comparing(feedback -> {
                    Period period = feedback.getDuration();
                    return period.ordinal(); // enum의 선언 순서를 기준으로 정렬
                }))
                .collect(Collectors.toList());

        return MyPageEvaluationDTO.builder()
                .keywords(keywordStatDTOs)
                .feedbacks(feedbackDTOs)
                .build();
    }
} 