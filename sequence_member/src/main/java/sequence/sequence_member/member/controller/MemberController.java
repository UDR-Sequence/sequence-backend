package sequence.sequence_member.member.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.MemberDTO;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.service.MemberSearchService;
import sequence.sequence_member.member.service.MemberService;
import sequence.sequence_member.member.dto.FindUsernameInputDTO;
import sequence.sequence_member.member.dto.FindUsernameOutputDTO;
import sequence.sequence_member.member.service.FindUsernameService;

import java.util.Map;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberSearchService memberSearchService;
    private final FindUsernameService findUsernameService;

    @PostMapping(value = "/join", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseData<String>> join(@RequestPart("memberDTO") @Valid MemberDTO memberDTO, Errors errors,
                                                        @RequestPart(name="authImgFile" ,required = false) MultipartFile authImgFile,
                                                        @RequestPart(name="portfolios",required=false) List<MultipartFile> portfolios){
        log.info("회원가입 요청 : /api/users/join POST request 발생");

        //회원가입 유효성 검사 실패 시
        if(errors.hasErrors()){
            log.error("회원가입 유효성 검증 실패");
            Map<String, String> validatorResult = memberService.validateHandling(errors);
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), validatorResult.values().toString()));
        }

        memberService.save(memberDTO,authImgFile,portfolios);
        return ResponseEntity.ok().body(ApiResponseData.success("회원가입이 완료되었습니다."));
    }

    // 아이디 찾기
    @PostMapping("/find_username")
    public ResponseEntity<ApiResponseData<FindUsernameOutputDTO>> findUsername(@Valid @RequestBody FindUsernameInputDTO inputDTO) {
        log.info("아이디 찾기 요청 : /api/users/find_username POST request 발생");

        FindUsernameOutputDTO result = findUsernameService.findUsername(inputDTO);
        return ResponseEntity.ok().body(ApiResponseData.success(result));
    }


    @GetMapping("/check_username")
    public ResponseEntity<ApiResponseData<String>> checkUser(@RequestParam(name = "username") String username){
        log.info("종복 아이디 체크 요청 : /api/users/check_username GET request 발생");

        // 파라미터 유효성 검사
        if (username == null || username.trim().isEmpty()) {
            log.error("유효성 검증 실패 : 아이디 공백");
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "아이디를 입력해주세요"));
        }

        //중복 아이디가 존재하는 경우
        if(memberService.checkUser(username)){
            log.error("중복 아이디 존재");
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "동일한 아이디가 이미 존재합니다."));
        }

        //아이디가 존재하지 않는 경우
        return ResponseEntity.ok().body(ApiResponseData.success("사용가능한 아이디 입니다."));
    }

    @GetMapping("/check_email")
    public ResponseEntity<ApiResponseData<String>> checkEmail(@RequestParam(name = "email") String email){
        log.info("종복 이메일 체크 요청 : /api/users/check_email GET request 발생");

        if(email == null || email.trim().isEmpty()){
            log.error("유효성 검증 실패 : 이메일 공백");

            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "이메일이 없습니다"));
        }

        if (memberService.checkEmail(email)) {
            log.error("중복 이메일 존재");

            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "동일한 이메일이 이미 존재합니다."));
        }

        return ResponseEntity.ok().body(ApiResponseData.success("사용가능한 이메일 입니다."));
    }

    @GetMapping("/check_nickname")
    public ResponseEntity<ApiResponseData<String>> checkNickname(@RequestParam(name = "nickname") String nickname){
        log.info("종복 닉네임 체크 요청 : /api/users/check_nickname GET request 발생");

        if(nickname == null || nickname.trim().isEmpty()){
            log.error("유효성 검증 실패 : 닉네임 공백");

            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "닉네임이 없습니다"));
        }

        if (memberService.checkNickname(nickname)) {
            log.error("중복 닉네임 존재");

            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "동일한 닉네임이 이미 존재합니다."));
        }

        return ResponseEntity.ok().body(ApiResponseData.success("사용가능한 닉네임 입니다."));
    }

    //닉네임으로 유저들 검색하는 컨트롤러
    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<List<String>>> searchMembers(@RequestParam(name = "nickname") String nickname, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        log.info("닉네임 검색 요청 : /api/users/search GET request 발생");

        return ResponseEntity.ok(ApiResponseData.success(memberSearchService.searchMemberNicknames(customUserDetails,nickname)));
    }
}
