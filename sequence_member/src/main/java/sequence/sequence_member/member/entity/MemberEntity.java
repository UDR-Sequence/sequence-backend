package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.global.utils.DataConvertor;
import sequence.sequence_member.member.dto.MemberDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "member")
public class MemberEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 45, unique = true)
    private String username;

    @Column(name="password", nullable = false, length = 150)
    private String password;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "birth", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birth;

    @Column(name="gender", nullable = false, columnDefinition = "ENUM('M','F')")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @Column(name = "phone", nullable = false, length = 13) // -를 포함한 13자리
    private String phone;

    @Column(name = "email", nullable = false, length = 45, unique = true)
    private String email;

    @Column(name="introduction", nullable = false)
    private String introduction;

    @Column(name="portfolio", length = 150)
    private String portfolio; // todo - 파일을 minio에 저장하고 url을 저장하는 방식으로 변경

    @Column(name="nickname", length = 45, unique = true)
    private String nickname;

    @Column(name="school_name", nullable = false)
    private String schoolName;

    @Column(name="profile_img", length = 400)
    private String profileImg; // todo - 파일을 minio에 저장하고 url을 저장하는 방식으로 변경

    @Column(nullable = false, columnDefinition = "BOOLEAN")
    private boolean isDeleted;

    // AwardEntity와의 일대다 관계 설정
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AwardEntity> awards=new ArrayList<>();

    // CareerEntity와의 일대다 관계 설정
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareerEntity> careers=new ArrayList<>();

    //ExperienceEntity와의 일대다 관계 설정
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienceEntity> experiences=new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "education_id")

    private EducationEntity education;

    public enum Gender{
        M,F
    }

    public static MemberEntity toMemberEntity(MemberDTO memberDTO){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(memberDTO.getUsername());
        memberEntity.setPassword(memberDTO.getPassword());
        memberEntity.setName(memberDTO.getName());
        memberEntity.setBirth(memberDTO.getBirth());
        memberEntity.setGender(memberDTO.getGender());
        memberEntity.setAddress(memberDTO.getAddress());
        memberEntity.setPhone(memberDTO.getPhone());
        memberEntity.setEmail(memberDTO.getEmail());
        memberEntity.setNickname(memberDTO.getNickname());
        memberEntity.setSchoolName(memberDTO.getSchoolName());
        memberEntity.setIntroduction(memberDTO.getIntroduction());
        memberEntity.setPortfolio(DataConvertor.listToString(memberDTO.getPortfolio()));
        return memberEntity;
    }
}
