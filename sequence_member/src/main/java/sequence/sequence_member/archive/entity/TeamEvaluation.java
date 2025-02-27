package sequence.sequence_member.archive.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.utils.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "team_evaluation")
public class TeamEvaluation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private ArchiveMember evaluator;    // 평가자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_id", nullable = false)
    private ArchiveMember evaluated;    // 피평가자

    @Column(nullable = true)
    private String feedback;            // 피드백 내용

    @Column(columnDefinition = "json", nullable = true)
    private String keyword;             // 키워드 (JSON 형태로 저장)

    @Column(nullable = true)
    private String lineFeedback;    // 한줄 피드백 필드 추가

    // 같은 Archive 내의 멤버끼리만 평가할 수 있도록 검증
    public boolean validateSameArchive() {
        return evaluator.getArchive().getId().equals(evaluated.getArchive().getId());
    }

    // 자기 자신을 평가할 수 없도록 검증
    public boolean validateSelfEvaluation() {
        return !evaluator.getId().equals(evaluated.getId());
    }

    public String getLineFeedback() {
        return this.lineFeedback;
    }
} 