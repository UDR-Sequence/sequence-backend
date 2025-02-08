package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sequence.sequence_member.global.enums.enums.AwardType;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.dto.MemberDTO;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "award")
@NoArgsConstructor
public class AwardEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AwardType awardType;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;

    @Column(nullable = false)
    private String organizer;

    @Column(nullable = false)
    private String awardName;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date awardDuration;

    public AwardEntity(
            AwardType awardType, String organizer,
            String awardName, Date awardDuration,
            MemberEntity member
    ) {
        this.awardType = awardType;
        this.organizer = organizer;
        this.awardName = awardName;
        this.awardDuration = awardDuration;
        this.member = member;
    }

    public static List<AwardEntity> toAwardEntity(MemberDTO memberDTO, MemberEntity memberEntity) {
        List<AwardEntity> awardEntities = new ArrayList<>();

        for (int i = 0; i < memberDTO.getAwards().size(); i++) {
            AwardEntity awardEntity = new AwardEntity();
            awardEntity.setAwardType(memberDTO.getAwards().get(i).getAwardType());
            awardEntity.setAwardName(memberDTO.getAwards().get(i).getAwardName());
            awardEntity.setOrganizer(memberDTO.getAwards().get(i).getOrganizer());
            awardEntity.setAwardDuration(memberDTO.getAwards().get(i).getAwardDuration());
            awardEntity.setMember(memberEntity);

            awardEntities.add(awardEntity);
        }
        return awardEntities;
    }
}
