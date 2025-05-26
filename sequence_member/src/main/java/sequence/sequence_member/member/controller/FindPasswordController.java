package sequence.sequence_member.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.FindPasswordInputDTO;
import sequence.sequence_member.member.dto.FindPasswordOutputDTO;
import sequence.sequence_member.member.dto.PasswordResetInputDTO;
import sequence.sequence_member.member.service.FindPasswordService;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class FindPasswordController {

    private final FindPasswordService findPasswordService;

    @PostMapping("/find-password")
    public ResponseEntity<FindPasswordOutputDTO> findPassword(
            @RequestBody @Valid FindPasswordInputDTO input) {

        String temporaryPassword = findPasswordService.findPassword(input);

        if (temporaryPassword != null) {
            return ResponseEntity.ok(
                    FindPasswordOutputDTO.success("임시 비밀번호가 이메일로 발송되었습니다."));
        } else {
                return ResponseEntity.ok(
                        FindPasswordOutputDTO.fail("일치하는 회원정보가 없습니다."));
        }
    }
}
