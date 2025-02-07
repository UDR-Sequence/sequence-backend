package sequence.sequence_member.archive.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import sequence.sequence_member.member.entity.MemberEntity;

@Entity
@Table(name = "ArchiveMember")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_member_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "archive_id", nullable = false)
    private ArchiveEntity archive;
}

