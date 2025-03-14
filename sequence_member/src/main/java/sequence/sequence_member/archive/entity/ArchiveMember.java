package sequence.sequence_member.archive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.entity.MemberEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "archive_member")
public class ArchiveMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    private Archive archive;

    @Column(name = "roles")
    private String roles;  // "백엔드,프론트엔드" 형태로 저장

    @Builder
    public ArchiveMember(Archive archive, MemberEntity member) {
        this.archive = archive;
        this.member = member;
    }

    public List<String> getRoleList() {
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(roles.split(","));
    }

    public void setRolesFromList(List<String> roleList) {
        if (roleList == null || roleList.isEmpty()) {
            this.roles = "";
            return;
        }
        this.roles = String.join(",", roleList);
    }
} 