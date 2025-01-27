package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "refresh")
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String refresh;
    private String expiration;
}
