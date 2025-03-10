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
@Table(name = "archive_comment")
public class ArchiveComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id", nullable = false)
    private Archive archive;

    @Column(nullable = false)
    private String writer;  // MemberEntity 관계 대신 writer 문자열로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ArchiveComment parent;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isDeleted;

    // 비즈니스 메서드
    public boolean isReply() {
        return parent != null;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void updateContent(String content) {
        this.content = content;
    }
} 