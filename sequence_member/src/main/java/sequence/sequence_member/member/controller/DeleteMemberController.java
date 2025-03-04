package sequence.sequence_member.member.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.DeleteInputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.member.service.DeleteService;
import sequence.sequence_member.member.service.MemberService;


@RestController
@RequiredArgsConstructor
public class DeleteMemberController {

    private final MemberService memberService;
    private final DeleteService deleteService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;


    // 사용자 탈퇴 API
    @DeleteMapping("/api/user/delete")
    public ResponseEntity<ApiResponseData<String>> deleteProcess(@RequestBody DeleteInputDTO deleteInputDTO, HttpServletRequest request) {
        //비밀번호 비교
        if (!deleteInputDTO.getPassword().equals(deleteInputDTO.getConfirm_password())) {
            throw new CanNotFindResourceException("동일한 비밀번호를 입력해주세요");
        }

        String refresh = deleteService.checkRefreshAndMember(request, deleteInputDTO.getUsername());

        MemberEntity member = memberService.GetUser(deleteInputDTO.getUsername());

        if (member.isDeleted() == true) {
            throw new CanNotFindResourceException("이미 탈퇴된 회원 입니다.");
        }

        //입력 비밀번호를 db와 비교
        if (!bCryptPasswordEncoder.matches(deleteInputDTO.getPassword(), member.getPassword())) {
            throw new CanNotFindResourceException("비밀번호가 일치하지 않습니다.");
        }

        //리프레시 토큰 제거
        //멤버 정보 제거
        //삭제한 멤버 받아오기
        //삭제한 user 정보 deleteMember 테이블에 저장
        deleteService.deleteRefreshAndMember(refresh);

        //성공 응답 반환
        return ResponseEntity.ok().body(ApiResponseData.success(member.getEmail(), "회원탈퇴 성공적으로 완료되었습니다."));
    }

    // 사용자 탈퇴 확인 API
    @GetMapping("/api/user/isDeleted")
    public ResponseEntity<ApiResponseData<Boolean>> isDeletedProcess(@RequestParam(name = "username") String username) {
        // 파라미터 유효성 검사
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "아이디를 입력해주세요"));
        }

        MemberEntity member = memberService.GetUser(username);

        //사용자를 찾을 수 없는 경우
        if (member == null) {
            return ResponseEntity.badRequest().body(ApiResponseData.failure(Code.CAN_NOT_FIND_RESOURCE.getCode(), "사용자를 찾을 수 없습니다"));
        }

        //사용자 탈퇴 상태 반환
        if(member.isDeleted() == false) {
            return ResponseEntity.ok().body(ApiResponseData.success(false, "탈퇴되지 않은 사용자 입니다."));
        }else{
            return ResponseEntity.ok().body(ApiResponseData.success(true, "탈퇴된 사용자 입니다."));
        }

    }

}

