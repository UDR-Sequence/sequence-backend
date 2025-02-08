package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import sequence.sequence_member.global.utils.BaseTimeEntity;

@Entity
@Data
@NoArgsConstructor
public class DeleteEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    public DeleteEntity(String username, String email) {
        this.username=username;
        this.email=email;
    }
}

