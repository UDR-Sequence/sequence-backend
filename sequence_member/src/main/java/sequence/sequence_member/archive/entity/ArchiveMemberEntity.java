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
    private Long archiveMemberId;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private MemberEntity memberId;

    @ManyToOne
    @JoinColumn(name = "archiveId", nullable = false)
    private ArchiveEntity archiveId;
}

