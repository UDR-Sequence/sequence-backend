package sequence.sequence_member.archive.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.utils.BaseTimeEntity;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "archive_bookmark",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"archive_id", "username"})
    })
public class ArchiveBookmark extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    private Archive archive;
    
    @Column(nullable = false)
    private String username;

    @Builder
    public ArchiveBookmark(Archive archive, String username) {
        this.archive = archive;
        this.username = username;
    }
}