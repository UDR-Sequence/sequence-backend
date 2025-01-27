package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;

import sequence.sequence_member.member.converter.DesiredJobConverter;
import sequence.sequence_member.member.converter.SkillCategoryConverter;

import sequence.sequence_member.member.dto.MemberDTO;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="education")
public class EducationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long educationId;

    @OneToOne
    @JoinColumn
    private MemberEntity member;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName;

    @Column(name = "major", nullable = false, length = 50)
    private String major;

    @Column(name = "entrance_date")
    @Temporal(TemporalType.DATE)
    private Date entranceDate;

    @Column(name = "graduation_date")
    @Temporal(TemporalType.DATE)
    private Date graduationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree", nullable = false)
    private Degree degree;

    @Convert(converter = SkillCategoryConverter.class)
    @Column(name = "skill_category")
    private List<SkillCategory> skillCategory;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "desired_job")
//    private DesiredJob desiredJob;

    @Convert(converter = DesiredJobConverter.class)
    @Column(name = "desired_job")
    private List<DesiredJob> desiredJob;

    public enum Degree {
        ENROLLMENT, LEAVE_OF_ABSENCE, GRADUATION, MASTER, DOCTORATE, EXPELLED, DROPOUT;
    }

    public enum SkillCategory {
        ADOBE_ILLUSTRATOR, ADOBE_PHOTOSHOP, ADOBE_INDESIGN, JAVASCRIPT, TYPESCRIPT;
    }

    public enum DesiredJob {
        UI_UX_DESIGN, FRONT_END, BACK_END, PM;
    }

    public static EducationEntity toEducationEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        EducationEntity educationEntity = new EducationEntity();

        educationEntity.setSchoolName(memberDTO.getSchool_name());
        educationEntity.setMajor(memberDTO.getMajor());
        educationEntity.setEntranceDate(memberDTO.getEntrance_date());
        educationEntity.setGraduationDate(memberDTO.getGraduation_date());
        educationEntity.setDegree(memberDTO.getDegree());
        educationEntity.setDesiredJob(memberDTO.getDesired_job());
        educationEntity.setSkillCategory(memberDTO.getSkill_category());
        educationEntity.setMember(memberEntity);

        return educationEntity;
    }
}
