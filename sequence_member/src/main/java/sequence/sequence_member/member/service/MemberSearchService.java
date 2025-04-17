package sequence.sequence_member.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.UserNotFindException;
import sequence.sequence_member.member.dto.CustomUserDetails;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberSearchService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<String> searchMemberNicknames(CustomUserDetails customUserDetails, String nickname){
        MemberEntity member = memberRepository.findByUsernameAndIsDeletedFalse(customUserDetails.getUsername())
                .orElseThrow(() -> new UserNotFindException("요청하는 유저가 존재하지 않습니다."));
        Pageable pageable = PageRequest.of(0, 20);  // 첫 페이지, 최대 20개
        List<String> nicknames = memberRepository.searchMemberNicknames(nickname, pageable);
        
        // 본인은 검색에서 제거
        for(String nick : nicknames){
            if(nick.equals(member.getNickname())){
                nicknames.remove(nick);
                break;
            }
        }
        return nicknames;
    }
}
