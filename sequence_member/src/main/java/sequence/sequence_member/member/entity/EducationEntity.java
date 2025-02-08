package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;

import lombok.NoArgsConstructor;
import sequence.sequence_member.global.enums.enums.Degree;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.global.enums.enums.Skill;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.converter.DesiredJobConverter;
import sequence.sequence_member.member.converter.SkillCategoryConverter;

import sequence.sequence_member.member.dto.MemberDTO;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="education")
@NoArgsConstructor
public class EducationEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id ;

    @OneToOne
    @JoinColumn
    private MemberEntity member;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName;

    @Column(name = "major", nullable = false, length = 50)
    private String major;

    @Column(nullable = false)
    private String grade; // 1학년, 2학년, 3학년, 4학년, 5학년, 6학년

    @Column(name = "entrance_date")
    @Temporal(TemporalType.DATE)
    private Date entranceDate;

    @Column(name = "graduation_date")
    @Temporal(TemporalType.DATE)
    private Date graduationDate;

    @Column(name = "degree", nullable = false)
    @Enumerated(EnumType.STRING)
    private Degree degree;

    @Convert(converter = SkillCategoryConverter.class)
    @Column(name = "skill_category")
    private List<Skill> skillCategory;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "desired_job")
//    private DesiredJob desiredJob;

    @Convert(converter = DesiredJobConverter.class)
    @Column(name = "desired_job")
    private List<sequence.sequence_member.global.enums.enums.ProjectRole> desiredJob;

    public EducationEntity(
            String schoolName, String major,
            String grade, Date entranceDate,
            Date graduationDate, Degree degree,
            List<Skill> skillCategory, List<ProjectRole> desiredJob,
            MemberEntity member
    ) {
       this.schoolName = schoolName;
       this.major = major;
       this.grade = grade;
       this.entranceDate = entranceDate;
       this.graduationDate = graduationDate;
       this.degree = degree;
       this.skillCategory = skillCategory;
       this.desiredJob = desiredJob;
       this.member = member;
    }

    public static EducationEntity toEducationEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        EducationEntity educationEntity = new EducationEntity();

        educationEntity.setSchoolName(memberDTO.getSchoolName());
        educationEntity.setMajor(memberDTO.getMajor());
        educationEntity.setGrade(memberDTO.getGrade());
        educationEntity.setEntranceDate(memberDTO.getEntranceDate());
        educationEntity.setGraduationDate(memberDTO.getGraduationDate());
        educationEntity.setDegree(memberDTO.getDegree());
        educationEntity.setDesiredJob(memberDTO.getDesiredJob());
        educationEntity.setSkillCategory(memberDTO.getSkillCategory());
        educationEntity.setMember(memberEntity);

        return educationEntity;
    }

    public void updateEducation(
            String schoolName, String major,
            String grade, Date entranceDate,
            Date graduationDate, Degree degree,
            List<Skill> skillCategory, List<ProjectRole> desiredJob
    ) {
        this.schoolName = schoolName;
        this.major = major;
        this.grade = grade;
        this.entranceDate = entranceDate;
        this.graduationDate = graduationDate;
        this.degree = degree;
        this.skillCategory = skillCategory;
        this.desiredJob = desiredJob;
    }
}
