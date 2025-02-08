package sequence.sequence_member.archive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Status;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEvaluationResponseDto {
    private Long id;                   // 평가 ID
    private Long evaluatorId;          // 평가자 ID
    private Long evaluatedId;          // 피평가자 ID
    private String feedback;           // 피드백 내용
    private String keyword;            // 키워드
    private String lineFeedback;       // 한줄 피드백
    private Status status;             // 평가 상태
    private LocalDateTime createdDateTime;    // 생성 시간
    private LocalDateTime modifiedDateTime;   // 수정 시간
} 