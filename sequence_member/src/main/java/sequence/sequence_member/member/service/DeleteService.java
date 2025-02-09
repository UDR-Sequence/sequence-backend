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
    public void deleteRefreshAndMember(HttpServletRequest request){
        //refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("refresh")){
                refresh = cookie.getValue();
            }
        }

        //토큰 존재여부 확인
        if (refresh == null) {
            throw new CanNotFindResourceException("토큰이 존재하지 않습니다.");
        }

        //refresh token 만료되었는지 확인
        try{
            jwtUtil.isExpired(refresh);
        }catch (ExpiredJwtException e){
            //만료 되었으면 다시 로그인을 진행하여, 회원탈퇴를 진행
            throw new CanNotFindResourceException("refresh 토큰이 만료되었습니다.");
        }

        //토큰이 refresh 인지 확인
        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("refresh")){
            //쿠기값이 유효하지 않습니다. 다시 로그인 해주세요
            throw new CanNotFindResourceException("쿠기값이 유효하지 않습니다. 다시 로그인 해주세요");
        }

        //DB에 refresh 토큰이 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if(!isExist){
            throw new CanNotFindResourceException("존재하지 않는 refresh 토큰 입니다.");
        }

        //refresh db에서 토큰 제거
        refreshRepository.deleteByRefresh(refresh);

        String username = jwtUtil.getUsername(refresh);
        MemberEntity deleteMember = memberRepository.findByUsername(username).get();

        awardRepository.deleteByMemberId(deleteMember.getId());
        careerRepository.deleteByMemberId(deleteMember.getId());
        experienceRepository.deleteByMemberId(deleteMember.getId());
        educationRepository.deleteByMemberId(deleteMember.getId());

        memberRepository.deleteByUsername(username);

        saveDeletedUser(deleteMember.getUsername(), deleteMember.getEmail());
    }

    @Transactional
    public void saveDeletedUser(String username, String email) {
        DeleteEntity deletedUser = new DeleteEntity(username, email);
        deletedRepository.save(deletedUser);
    }


}

