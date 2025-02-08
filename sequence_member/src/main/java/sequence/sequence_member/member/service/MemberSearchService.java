package sequence.sequence_member.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberSearchService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<String> searchMemberNicknames(String nickname){
        Pageable pageable = PageRequest.of(0, 20);  // 첫 페이지, 최대 20개
        List<String> strings = memberRepository.searchMemberNicknames(nickname, pageable);
        return strings;
    }
}
