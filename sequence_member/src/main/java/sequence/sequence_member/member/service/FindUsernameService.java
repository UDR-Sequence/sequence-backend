package sequence.sequence_member.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.exception.BAD_REQUEST_EXCEPTION;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.FindUsernameInputDTO;
import sequence.sequence_member.member.dto.FindUsernameOutputDTO;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FindUsernameService {
    private final MemberRepository memberRepository;

    /*
     * 아이디 찾기 서비스 로직
     * @param inputDTO 사용자 입력 정보
     * @return FindUsernameOutputDTO (username + 가입일자)
     */
    public FindUsernameOutputDTO findUsername(FindUsernameInputDTO inputDTO) {
        MemberEntity member = memberRepository.findMemberForFindUsername(
                inputDTO.getName(),
                inputDTO.getBirth(),
                inputDTO.getGender(),
                inputDTO.getPhone(),
                inputDTO.getEmail()
        ).orElseThrow(() -> new UserNotFindException("존재하지 않는 회원 정보입니다."));

        return new FindUsernameOutputDTO(
                member.getName(),
                member.getUsername(),
                member.getCreatedDateTime().toLocalDate()
        );
    }
}
