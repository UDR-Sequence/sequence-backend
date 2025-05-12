package sequence.sequence_member.member.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.FindPasswordInputDTO;
import sequence.sequence_member.member.dto.PasswordResetInputDTO;
import sequence.sequence_member.member.service.PasswordResetService;

@RestController
@RequestMapping("/api/pw-reset")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/request-reset")
    public ResponseEntity<ApiResponseData<Void>> requestReset(
            @Valid @RequestBody FindPasswordInputDTO findPasswordInputDTO) {
        passwordResetService.createPasswordResetToken(findPasswordInputDTO);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseData<Void>> resetPassword(
            @Valid @RequestBody PasswordResetInputDTO passwordResetInputDTO) {
                passwordResetService.resetPassword(passwordResetInputDTO);

                return ResponseEntity.ok(ApiResponseData.success(null));
    }

}
