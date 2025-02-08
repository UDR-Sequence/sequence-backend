package sequence.sequence_member.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import sequence.sequence_member.member.dto.MemberDTO;
import sequence.sequence_member.member.entity.*;
import sequence.sequence_member.member.repository.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void  save(MemberDTO memberDTO){

        //memberDTO의 비밀번호 값을 암호화하여 memberDTO에 저장하고 memberEntity로 변환하여 저장
        //엔티티 클래스는 데이터베이스 구조를 반영해야 하며, 비즈니스 로직(회원가입, 로그인, 비밀번호 암호화)과 분리되어야 한다.
        //그러므로, 엔티티 클래스에서 비밀번호 암호화를 하는 것은 적절하지 않다고 판단하여 service 단에서 처리.
        memberDTO.setPassword(bCryptPasswordEncoder.encode(memberDTO.getPassword()));

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        //먼저 member 정보를 저장하고 나중에 외래키 값을 저장하기 위해서 멤버 정보를 먼저 저장
        memberRepository.save(memberEntity);

        MemberEntity memberEntityCopy =  memberRepository.findByUsername(memberDTO.getUsername()).get();

        List<AwardEntity> awardEntities = AwardEntity.toAwardEntity(memberDTO, memberEntityCopy);
        List<ExperienceEntity> experienceEntities = ExperienceEntity.toExperienceEntity(memberDTO, memberEntityCopy);
        List<CareerEntity> careerEntities  = CareerEntity.toCareerEntity(memberDTO, memberEntityCopy);
        EducationEntity educationEntity = EducationEntity.toEducationEntity(memberDTO, memberEntityCopy);

        // 관계 설정
        educationEntity.setMember(memberEntityCopy);
        memberEntityCopy.setEducation(educationEntity);

        experienceRepository.saveAll(experienceEntities);
        careerRepository.saveAll(careerEntities);
        awardRepository.saveAll(awardEntities);
        educationRepository.save(educationEntity);
        memberRepository.save(memberEntityCopy); // MemberEntity도 다시 저장하여 FK 설정
    }

    /* 회원가입 시, 유효성 체크 */
    public Map<String, String> validateHandling(Errors errors) {
        if (!errors.hasErrors()) {
            // Errors가 없을 경우 빈 Map 반환
            //외부에서 수정못하도록 불변객체로 변환
            return Map.of();
        }

        Map<String, String> validatorResult = new HashMap<>();
        for (FieldError error : errors.getFieldErrors()) {
            validatorResult.put(error.getField(), error.getDefaultMessage());
        }
        //외부에서 수정못하도록 불변 객체로 변환
        return Collections.unmodifiableMap(validatorResult);
    }

    public boolean checkUser(String username){

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        return memberRepository.findByUsername(username).isPresent();
    }
}
