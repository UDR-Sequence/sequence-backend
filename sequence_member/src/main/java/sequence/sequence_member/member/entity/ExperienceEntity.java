package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sequence.sequence_member.global.enums.enums.ExperienceType;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.dto.MemberDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "experience")
@NoArgsConstructor
public class ExperienceEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExperienceType experienceType;

    @Column(nullable = false)
    private String experienceName;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Column(nullable = false)
    private String experienceDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public ExperienceEntity(
            ExperienceType experienceType, String experienceName,
            LocalDate startDate,LocalDate endDate, String experienceDescription,
            MemberEntity member
    ) {
        this.experienceType = experienceType;
        this.experienceName = experienceName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.experienceDescription = experienceDescription;
        this.member = member;
    }

    public static List<ExperienceEntity> toExperienceEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        List<ExperienceEntity> experienceEntities = new ArrayList<>();

        //dto에서 입력받은 경력 사항의 수만큼 엔티티를 저장하기 위해서 반복문 처리
        for(int i=0;i<memberDTO.getExperiences().size();i++){
            //dto로 부터 입력받은 엔티티들을 하나하나씩 저장하기 위해서 엔티티 객체를 새로 만들어서 리스트에 저장
            ExperienceEntity experienceEntity = new ExperienceEntity();
            experienceEntity.setExperienceType(memberDTO.getExperiences().get(i).getExperienceType());
            experienceEntity.setStartDate(memberDTO.getExperiences().get(i).getStartDate());
            experienceEntity.setEndDate(memberDTO.getExperiences().get(i).getEndDate());
            experienceEntity.setExperienceDescription(memberDTO.getExperiences().get(i).getExperienceDescription());
            experienceEntity.setExperienceName(memberDTO.getExperiences().get(i).getExperienceName());
            experienceEntity.setMember(memberEntity);

            experienceEntities.add(experienceEntity);
        }

        return experienceEntities;
    }
}
