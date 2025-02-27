package sequence.sequence_member.member.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.AcceptProjectOutputDTO;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteProjectOutputDTO;
import sequence.sequence_member.member.dto.MemberDTO;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.service.InviteAccessService;
import sequence.sequence_member.member.service.MemberSearchService;
import sequence.sequence_member.member.service.MemberService;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final InviteAccessService inviteAccessService;
    private final MemberSearchService memberSearchService;

    @PostMapping(value = "/join", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseData<String>> join(@RequestPart("memberDTO") @Valid MemberDTO memberDTO, Errors errors,
                                                        @RequestPart(name="authImgFile" ,required = false) MultipartFile authImgFile){
        //회원가입 유효성 검사 실패 시
        if(errors.hasErrors()){
            Map<String, String> validatorResult = memberService.validateHandling(errors);

            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), validatorResult.values().toString()));
        }

        memberService.save(memberDTO,authImgFile);
        return ResponseEntity.ok().body(ApiResponseData.success("회원가입이 완료되었습니다."));
    }

    @GetMapping("/check_username")
    public ResponseEntity<ApiResponseData<String>> checkUser(@RequestParam(name = "username") String username){
        // 파라미터 유효성 검사
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "아이디를 입력해주세요"));
        }

        //중복 아이디가 존재하는 경우
        if(memberService.checkUser(username)){
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "동일한 아이디가 이미 존재합니다."));
        }

        //아이디가 존재하지 않는 경우
        return ResponseEntity.ok().body(ApiResponseData.success("사용가능한 아이디 입니다."));
    }

    @GetMapping("/check_email")
    public ResponseEntity<ApiResponseData<String>> checkEmail(@RequestParam(name = "email") String email){
        if(email == null || email.trim().isEmpty()){
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "이메일이 없습니다"));
        }

        if (memberService.checkEmail(email)) {
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "동일한 이메일이 이미 존재합니다."));
        }

        return ResponseEntity.ok().body(ApiResponseData.success("사용가능한 이메일 입니다."));
    }

    @GetMapping("/check_nickname")
    public ResponseEntity<ApiResponseData<String>> checkNickname(@RequestParam(name = "nickname") String nickname){
        if(nickname == null || nickname.trim().isEmpty()){
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "닉네임이 없습니다"));
        }

        if (memberService.checkNickname(nickname)) {
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.INVALID_INPUT.getCode(), "동일한 닉네임이 이미 존재합니다."));
        }

        return ResponseEntity.ok().body(ApiResponseData.success("사용가능한 닉네임 입니다."));
    }

    //유저가 초대받은 프로젝트 목록을 조회하는 컨트롤러
    @GetMapping("/invited-projects")
    public ResponseEntity<ApiResponseData<List<InviteProjectOutputDTO>>> getInvitedProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok(ApiResponseData.success(inviteAccessService.getInvitedProjects(customUserDetails)));
    }

    //유저가 초대받은 프로젝트에 승인하는 컨트롤러
    @PostMapping("/invited-projects/{projectInvitedMemberId}")
    public ResponseEntity<ApiResponseData<String>> acceptInvite(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long projectInvitedMemberId){
        inviteAccessService.acceptInvite(customUserDetails, projectInvitedMemberId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }

    //유저가 초대받은 프로젝트에 거절하는 컨트롤러
    @DeleteMapping("/invited-projects/{projectInvitedMemberId}")
    public ResponseEntity<ApiResponseData<String>> rejectInvite(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long projectInvitedMemberId){
        inviteAccessService.rejectInvite(customUserDetails, projectInvitedMemberId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }

    //유저가 승인한(참여하는) 프로젝트 목록을 조회하는 컨트롤러
    @GetMapping("/accepted-projects")
    public ResponseEntity<ApiResponseData<List<AcceptProjectOutputDTO>>> getAcceptedProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok(ApiResponseData.success(inviteAccessService.getAcceptedProjects(customUserDetails)));
    }

    //닉네임으로 유저들 검색하는 컨트롤러
    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<List<String>>> searchMembers(@RequestParam(name = "nickname") String nickname){
        return ResponseEntity.ok(ApiResponseData.success(memberSearchService.searchMemberNicknames(nickname)));
    }

}
