package sequence.sequence_member.report.entity;

import jakarta.persistence.*;
import lombok.*;
import sequence.sequence_member.global.utils.BaseTimeEntity;

import java.util.Arrays;
import java.util.List;

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


    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_target")
    private ReportTarget reportTarget;

    private Long targetId;

    @Column(columnDefinition = "TEXT", length = 500)
    private String reportContent;


    @Getter
    public enum ReportType {
        INAPPROPRIATE_PROFILE("부적절한 프로필 이미지입니다."),
        SPAM("스팸 또는 홍보성 게시글입니다."),
        ABUSIVE_LANGUAGE("욕설 및 부적절한 언어를 사용했습니다."),
        FALSE_INFORMATION("허위 정보 또는 사기성 게시글입니다."),
        OTHER("기타 사유로 신고합니다.");

        private final String description;

        ReportType(String description) {
            this.description = description;
        }

        public static ReportType fromDescription(String description) {
            return Arrays.stream(ReportType.values())
                    .filter(type -> type.getDescription().equals(description))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 신고 유형입니다: " + description));
        }
    }

    @Getter
    public enum ReportTarget {
        USER("유저"),
        PROJECT_COMMENT("프로젝트 댓글"),
        ARCHIVE_COMMENT("아카이브 댓글"),
        PROJECT("프로젝트"),
        ARCHIVE("아카이브");

        private final String description;

        ReportTarget(String description) {
            this.description = description;
        }

        public static ReportTarget fromDescription(String desc) {
            return Arrays.stream(values())
                    .filter(t -> t.description.equals(desc))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 설명: " + desc));
        }
    }

}
