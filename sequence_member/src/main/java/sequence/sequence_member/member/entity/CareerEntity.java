package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.dto.MemberDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="career")
@NoArgsConstructor
public class CareerEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;

    @Column(name = "company_name", length = 100,nullable = false)
    private String companyName;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;


    @Column(nullable = false)
    private String careerDescription;

    public CareerEntity(
            String companyName, LocalDate startDate, LocalDate endDate,
            String careerDescription, MemberEntity member
    ) {
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.careerDescription = careerDescription;
        this.member = member;
    }

    public static List<CareerEntity> toCareerEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        List<CareerEntity> careerEntities = new ArrayList<>();

        for(int i=0;i<memberDTO.getCareers().size();i++){
            CareerEntity careerEntity = new CareerEntity();

            careerEntity.setCompanyName(memberDTO.getCareers().get(i).getCompanyName());
            careerEntity.setCareerDescription(memberDTO.getCareers().get(i).getCareerDescription());
            careerEntity.setStartDate(memberDTO.getCareers().get(i).getStartDate());
            careerEntity.setEndDate(memberDTO.getCareers().get(i).getEndDate());
            careerEntity.setMember(memberEntity);

            careerEntities.add(careerEntity);
        }
        return careerEntities;
    }
}
