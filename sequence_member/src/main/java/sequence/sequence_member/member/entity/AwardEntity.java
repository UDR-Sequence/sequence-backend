package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.dto.MemberDTO;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@Table(name = "award")
public class AwardEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;

    @Column
    private String awardName;

    @Column
    @Temporal(TemporalType.DATE)
    private Date awardDuration;

    @Column
    private String awardDescription;


    public static List<AwardEntity> toAwardEntity(MemberDTO memberDTO, MemberEntity memberEntity) {
        List<AwardEntity> awardEntities = new ArrayList<>();

        for (int i = 0; i < memberDTO.getAwards().size(); i++) {
            AwardEntity awardEntity = new AwardEntity();

            awardEntity.setAwardName(memberDTO.getAwards().get(i).getAwardName());
            awardEntity.setAwardDescription(memberDTO.getAwards().get(i).getAwardDescription());
            awardEntity.setAwardDuration(memberDTO.getAwards().get(i).getAwardDuration());
            awardEntity.setMember(memberEntity);

            awardEntities.add(awardEntity);
        }
        return awardEntities;

    }

}
