package sequence.sequence_member.project.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.MeetingOption;
import sequence.sequence_member.global.enums.enums.Period;
import sequence.sequence_member.global.enums.enums.Step;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.entity.MemberEntity;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "project")
public class Project extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Period period;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private int personnel;

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false)
    private String skills;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingOption meetingOption;

    @Column(nullable = false)
    private Step step;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String introduce;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String article;

    @Column
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private MemberEntity writer;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectMember> members;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectInvitedMember> invitedMembers;

    // List<ProjectMemberEntity> 에서 username만을 가지고 있는 List<String> 반환
    public List<String> getMemberUsernames() {
        return this.getMembers() // List<ProjectMemberEntity> 반환
                .stream() // Stream<ProjectMemberEntity>
                .map(projectMemberentity-> projectMemberentity.getMember().getUsername()) // 각 객체의 username 필드 추출
                .collect(Collectors.toList()); // List<String>으로 변환
    }
}