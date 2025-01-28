package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.dto.MemberDTO;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name = "experience")
public class ExperienceEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long experienceId;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;

    @Column
    private String activityName;

    @Column
    @Temporal(TemporalType.DATE)
    private Date activityDuration;

    @Column
    private String activityDescription;


    public static List<ExperienceEntity> toExperienceEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        List<ExperienceEntity> experienceEntities = new ArrayList<>();

        //dto에서 입력받은 경력 사항의 수만큼 엔티티를 저장하기 위해서 반복문 처리
        for(int i=0;i<memberDTO.getExperiences().size();i++){
            //dto로 부터 입력받은 엔티티들을 하나하나씩 저장하기 위해서 엔티티 객체를 새로 만들어서 리스트에 저장
            ExperienceEntity experienceEntity = new ExperienceEntity();
            experienceEntity.setActivityDuration(memberDTO.getExperiences().get(i).getActivityDuration());
            experienceEntity.setActivityDescription(memberDTO.getExperiences().get(i).getActivityDescription());
            experienceEntity.setActivityName(memberDTO.getExperiences().get(i).getActivityName());
            experienceEntity.setMember(memberEntity);

            experienceEntities.add(experienceEntity);
        }

        return experienceEntities;

    }
}
