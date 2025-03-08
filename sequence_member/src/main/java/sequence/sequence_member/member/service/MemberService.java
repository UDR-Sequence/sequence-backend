package sequence.sequence_member.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.utils.MultipartUtil;
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

    private final String SUFFIX = "auth";
    @Value("${minio.bucketName}")
    private  String BUCKET_NAME;

    private final MultipartUtil multipartUtil;
    private final MemberRepository memberRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void save(MemberDTO memberDTO, MultipartFile authImgFile){

        memberDTO.setPassword(bCryptPasswordEncoder.encode(memberDTO.getPassword()));

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);

        //파일명 생성
        String fileName = multipartUtil.determineFileName(authImgFile, memberDTO.getUsername(), SUFFIX, BUCKET_NAME);
        memberEntity.setProfileImg(fileName);

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

    //아이디 중복체크
    public boolean checkUser(String username){

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("아이디를 입력해주세요");
        }
        return memberRepository.findByUsername(username).isPresent();
    }

    //닉네임 중복 체크
    public boolean checkNickname(String nickname){
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요");
        }
        return memberRepository.findByNickname(nickname).isPresent();
    }

    //이메일 중복체크
    public boolean checkEmail(String email){
        if(email == null || email.trim().isEmpty()){
            throw new IllegalArgumentException("이메일을 입력해주세요");
        }
        return memberRepository.findByEmail(email).isPresent();

    }

    public MemberEntity GetUser(String username){
        if(username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }
        return memberRepository.findByUsername(username).get();
    }
}
