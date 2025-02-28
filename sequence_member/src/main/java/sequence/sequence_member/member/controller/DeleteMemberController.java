package sequence.sequence_member.member.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.member.dto.DeleteInputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.service.DeleteService;
import sequence.sequence_member.member.service.MemberService;


@RestController
@RequiredArgsConstructor
public class DeleteMemberController {

    private final MemberService memberService;
    private final DeleteService deleteService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    // 사용자 탈퇴 API
    @DeleteMapping("/api/user/delete")
    public ResponseEntity<ApiResponseData<String>> deleteProcess(@RequestBody DeleteInputDTO deleteInputDTO, HttpServletRequest request) {
        //비밀번호 비교
        if (!deleteInputDTO.getPassword().equals(deleteInputDTO.getConfirm_password())) {
            throw new CanNotFindResourceException("동일한 비밀번호를 입력해주세요");
        }

        String refresh = deleteService.checkRefreshAndMember(request, deleteInputDTO.getUsername());

        MemberEntity member = memberService.GetUser(deleteInputDTO.getUsername());

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



}

