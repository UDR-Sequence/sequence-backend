package sequence.sequence_member.member.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteInputDTO {
    private String username;
    private String password;
    private String confirm_password;
}
