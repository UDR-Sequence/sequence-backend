package sequence.sequence_member.archive.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

import java.util.List;

@Entity
@Table(name = "Archive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "field", nullable = false)
    private String field;

    // TinyInt(0,1) 상태로 저장
    // 0: 비활성, 1: 활성
    @Column(name = "status", nullable = false)
    private Byte status;

    // ArchiveMember와 1:N 관계
    @OneToMany(mappedBy = "archive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArchiveMemberEntity> archiveMembers;

    public boolean isStatus() {
        return status != null && status == 1;
    }
}
