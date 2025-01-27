package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;
import sequence.sequence_member.member.dto.MemberDTO;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@Table(name="career")
public class CareerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long career_id;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;

    @Column(name = "career_name", length = 100)
    private String careerName;

    @Column
    @Temporal(TemporalType.DATE)
    private Date careerDuration;

    @Column
    private String careerDescription;


    public static List<CareerEntity> toCareerEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        List<CareerEntity> careerEntities = new ArrayList<>();

        for(int i=0;i<memberDTO.getCareers().size();i++){
            CareerEntity careerEntity = new CareerEntity();

            careerEntity.setCareerName(memberDTO.getCareers().get(i).getCareerName());
            careerEntity.setCareerDescription(memberDTO.getCareers().get(i).getCareerDescription());
            careerEntity.setCareerDuration(memberDTO.getCareers().get(i).getCareerDuration());
            careerEntity.setMember(memberEntity);

            careerEntities.add(careerEntity);
        }
        return careerEntities;

    }
}
