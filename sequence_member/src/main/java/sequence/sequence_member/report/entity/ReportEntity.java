package sequence.sequence_member.report.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.utils.BaseTimeEntity;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "report")
public class ReportEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String reporter;

    @Column(nullable = false)
    private String reportType;

    @Column(columnDefinition = "TEXT", length = 500)
    private String reportContent;
}
