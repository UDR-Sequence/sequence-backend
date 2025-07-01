package sequence.sequence_member.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.FindPasswordInputDTO;
import sequence.sequence_member.member.service.FindPasswordService;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class FindPasswordController {

    private final FindPasswordService findPasswordService;

    @PostMapping("/find_password")
    public ResponseEntity<ApiResponseData<String>> findPassword(
            @RequestBody @Valid FindPasswordInputDTO input) {
        log.info("비밀번호 찾기 요청 : /api/member/find_password POST request 발생");

        String temporaryPassword = findPasswordService.findPassword(input);

        return ResponseEntity.ok().body(ApiResponseData.of(Code.SUCCESS.getCode(), "임시비밀번호가 발급되었습니다." , temporaryPassword));

    }
}
