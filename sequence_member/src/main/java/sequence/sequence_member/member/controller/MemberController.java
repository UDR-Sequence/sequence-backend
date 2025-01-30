package sequence.sequence_member.member.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.dto.InviteOutputDTO;
import sequence.sequence_member.member.dto.MemberDTO;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.service.InviteAccessService;
import sequence.sequence_member.member.service.MemberService;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final InviteAccessService inviteAccessService;

    @PostMapping("/join")
    public ApiResponseData<String> join(@RequestBody @Valid MemberDTO memberDTO, Errors errors){
        //회원가입 유효성 검사 실패 시
        if(errors.hasErrors()){
            Map<String, String> validatorResult = memberService.validateHandling(errors);

            //ResponseMsg errorResponse = new ResponseMsg(400, "Validation Failed", validatorResult);
            return ApiResponseData.failure(400, validatorResult.values().toString());
        }

        memberService.save(memberDTO);
        //ResponseMsg responseMsg = new ResponseMsg(200, "회원가입이 완료되었습니다.", null);
        return ApiResponseData.success("회원가입이 완료되었습니다.");
    }


    //시큐리티 컨텍스트에 저장된 authtoken을 확인하는 용도로 컨트롤러 작성
    //jwt는 stateless이지만, 생성되었을때, 세션을 통해 잠시 저장되었다가 삭제된다. (stateless로 봐도 무방하다)
    @GetMapping("/")
    public String mainPage(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return "mainPage " + username;
    }

    @RequestMapping("/check_username")
    public ApiResponseData<String> checkUser(@RequestParam(name = "username",required = false) String username){
        // 파라미터 유효성 검사
        if (username == null || username.trim().isEmpty()) {
            return ApiResponseData.failure(400,"아이디를 입력해주세요");
        }

        //중복 아이디가 존재하는 경우
        if(memberService.checkUser(username)){
            return ApiResponseData.failure(400,"동일한 아이디가 이미 존재합니다.");
        }

        //아이디가 존재하지 않는 경우
        return ApiResponseData.success("사용가능한 아이디 입니다.");
    }

    @GetMapping("/invited-projects")
    public ApiResponseData<List<InviteOutputDTO>> getInvitedProjects(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ApiResponseData.success(inviteAccessService.getInvitedProjects(customUserDetails));
    }

}
