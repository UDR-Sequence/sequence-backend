package sequence.sequence_member.archive.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import sequence.sequence_member.member.entity.MemberEntity;

import java.time.LocalDate;

@Entity
@Table(name = "TeamEvaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamEvaluationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluationId", nullable = false)
    private Long evaluationId;

    @ManyToOne
    @JoinColumn(name = "evaluatorId", nullable = false)
    @Comment("평가자 ID")
    private MemberEntity evaluatorId;

    @ManyToOne
    @JoinColumn(name = "evaluateeId", nullable = false)
    @Comment("피평가자 ID")
    private MemberEntity evaluateeId;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "keyword", columnDefinition = "json")
    @Comment("키워드")
    private String keyword;

    @Column(name = "lineFeedback")
    @Comment("한줄평")
    private String lineFeedback;

    @Column(name = "evaluationDate", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate evaluationDate;
}
