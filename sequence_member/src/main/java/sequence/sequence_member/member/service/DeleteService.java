package sequence.sequence_member.member.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.exception.CanNotFindResourceException;
import sequence.sequence_member.member.entity.DeleteEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.jwt.JWTUtil;
import sequence.sequence_member.member.repository.*;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeleteService {

    private final DeleteRepository deletedRepository;
    private final MemberRepository memberRepository;

    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;


    @Transactional
    public void deleteRefreshAndMember(String refresh){

        //refresh db에서 토큰 제거
        refreshRepository.deleteByRefresh(refresh);

        //username 가져오기
        String username = jwtUtil.getUsername(refresh);
        MemberEntity deleteMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CanNotFindResourceException("사용자를 찾을 수 없습니다."));

        //회원 정보 삭제
        awardRepository.deleteByMemberId(deleteMember.getId());
        careerRepository.deleteByMemberId(deleteMember.getId());
        experienceRepository.deleteByMemberId(deleteMember.getId());
        educationRepository.deleteByMemberId(deleteMember.getId());

        memberRepository.deleteByUsername(username);

        saveDeletedUser(deleteMember.getUsername(), deleteMember.getEmail());
    }

    public String checkRefreshAndMember(HttpServletRequest request, String username){
        // 쿠키 확인
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CanNotFindResourceException("로그인이 필요합니다.");
        }

        // Refresh 토큰 찾기
        String refresh = null;
        for (Cookie cookie : cookies) {
            if ("refresh".equals(cookie.getName())) {
                refresh = cookie.getValue();
            }
        }

        // 토큰 존재 여부 확인
        if (refresh == null) {
            throw new CanNotFindResourceException("토큰이 존재하지 않습니다. 다시 로그인해주세요.");
        }

        // Refresh Token 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new CanNotFindResourceException("refresh 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }

        // Refresh Token 유효성 검증
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new CanNotFindResourceException("쿠키 값이 유효하지 않습니다. 다시 로그인 해주세요.");
        }

        // DB에 Refresh Token 존재하는지 확인
        boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new CanNotFindResourceException("존재하지 않는 refresh 토큰입니다. 다시 로그인해주세요.");
        }

        //Refresh Token과 username이 일치하는지 확인
        String tokenUsername = jwtUtil.getUsername(refresh);
        if (!Objects.equals(tokenUsername, username)) {
            throw new CanNotFindResourceException("요청한 사용자와 로그인된 사용자가 다릅니다.");
        }

        return refresh;
    }

    @Transactional
    public void saveDeletedUser(String username, String email) {
        DeleteEntity deletedUser = new DeleteEntity(username, email);
        deletedRepository.save(deletedUser);
    }

}

