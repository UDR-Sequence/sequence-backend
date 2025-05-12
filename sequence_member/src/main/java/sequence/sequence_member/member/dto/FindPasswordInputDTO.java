package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindPasswordInputDTO {
    @Email @NotBlank
    private String email;
}
