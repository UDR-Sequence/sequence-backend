package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sequence.sequence_member.global.utils.BaseTimeEntity;

@Entity
@Getter
@Setter
@Table(name = "refresh")
public class RefreshEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String refresh;
    private String expiration;
}
