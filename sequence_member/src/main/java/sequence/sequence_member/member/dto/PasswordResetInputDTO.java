package sequence.sequence_member.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetInputDTO {
    @NotBlank
    private String token;

    @NotBlank @Size(min = 8, max = 32)
    private String newPassword;
}
