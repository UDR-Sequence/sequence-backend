package sequence.sequence_member.mypage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sequence.sequence_member.global.utils.DataConvertor;
import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.EducationEntity;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.AwardRepository;
import sequence.sequence_member.member.repository.CareerRepository;
import sequence.sequence_member.member.repository.EducationRepository;
import sequence.sequence_member.member.repository.ExperienceRepository;
import sequence.sequence_member.mypage.dto.MyPageRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageUpdateService {

    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;

    /**
     * 주어진 사용자 정보를 바탕으로 마이페이지를 업데이트합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 마이페이지 정보
     */
    public void updateProfile(MemberEntity member, MyPageRequestDTO myPageDTO) {
        updateBasicInfo(member, myPageDTO);        // 기본 정보 업데이트
        updateAwards(member, myPageDTO);           // 수상 경력 업데이트
        updateCareers(member, myPageDTO);          // 경력 업데이트
        updateExperiences(member, myPageDTO);      // 경험 업데이트
        updateEducation(member, myPageDTO);        // 교육 정보 업데이트
    }

    /**
     * 사용자 기본 정보를 업데이트합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 기본 정보
     */
    private void updateBasicInfo(MemberEntity member, MyPageRequestDTO myPageDTO) {
        member.setName(myPageDTO.getName());
        member.setBirth(myPageDTO.getBirth());
        member.setGender(myPageDTO.getGender());
        member.setAddress(myPageDTO.getAddress());
        member.setPhone(myPageDTO.getPhone());
        member.setIntroduction(myPageDTO.getIntroduction());
        member.setPortfolio(DataConvertor.listToString(myPageDTO.getPortfolio()));
        member.setNickname(myPageDTO.getNickname());
    }

    /**
     * 사용자의 수상 경력을 업데이트합니다.
     * 기존 수상 경력을 삭제하고 새로운 경력을 추가합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 수상 경력 정보
     */
    private void updateAwards(MemberEntity member, MyPageRequestDTO myPageDTO) {
        List<AwardEntity> existingAwards = awardRepository.findByMember(member);
        List<AwardEntity> newAwards = myPageDTO.getAwards().stream()
                .map(dto -> new AwardEntity(dto.getAwardType(), dto.getOrganizer(), dto.getAwardName(), dto.getAwardDuration(), member))
                .collect(Collectors.toList());

        awardRepository.deleteAll(existingAwards);
        awardRepository.saveAll(newAwards);
    }

    /**
     * 사용자의 경력을 업데이트합니다.
     * 기존 경력을 삭제하고 새로운 경력을 추가합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 경력 정보
     */
    private void updateCareers(MemberEntity member, MyPageRequestDTO myPageDTO) {
        List<CareerEntity> existingCareers = careerRepository.findByMember(member);  // 기존 경력 조회
        List<CareerEntity> newCareers = myPageDTO.getCareers().stream()               // 새로운 경력으로 업데이트
                .map(dto -> new CareerEntity(dto.getCompanyName(), dto.getStartDate(), dto.getEndDate(), dto.getCareerDescription(), member))
                .collect(Collectors.toList());

        careerRepository.deleteAll(existingCareers);
        careerRepository.saveAll(newCareers);
    }

    /**
     * 사용자의 경험을 업데이트합니다.
     * 기존 경험을 삭제하고 새로운 경험을 추가합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 경험 정보
     */
    private void updateExperiences(MemberEntity member, MyPageRequestDTO myPageDTO) {
        List<ExperienceEntity> existingExperiences = experienceRepository.findByMember(member);  // 기존 경험 조회
        List<ExperienceEntity> newExperiences = myPageDTO.getExperiences().stream()               // 새로운 경험으로 업데이트
                .map(dto -> new ExperienceEntity(dto.getExperienceType(), dto.getExperienceName(), dto.getStartDate(), dto.getEndDate(), dto.getExperienceDescription(), member))
                .collect(Collectors.toList());

        experienceRepository.deleteAll(existingExperiences);
        experienceRepository.saveAll(newExperiences);
    }

    /**
     * 사용자의 교육 정보를 업데이트합니다.
     * 사용자가 이미 교육 정보를 가지고 있으면 해당 정보를 업데이트하고, 그렇지 않으면 새로 추가합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 교육 정보
     */
    private void updateEducation(MemberEntity member, MyPageRequestDTO myPageDTO) {
        educationRepository.findByMember(member).ifPresentOrElse(
                education -> education.updateEducation(
                        myPageDTO.getSchoolName(),
                        myPageDTO.getMajor(),
                        myPageDTO.getGrade(),
                        myPageDTO.getEntranceDate(),
                        myPageDTO.getGraduationDate(),
                        myPageDTO.getDegree(),
                        myPageDTO.getSkillCategory(),
                        myPageDTO.getDesiredJob()
                ),
                () -> {
                    EducationEntity newEducation = new EducationEntity(
                            myPageDTO.getSchoolName(),
                            myPageDTO.getMajor(),
                            myPageDTO.getGrade(),
                            myPageDTO.getEntranceDate(),
                            myPageDTO.getGraduationDate(),
                            myPageDTO.getDegree(),
                            myPageDTO.getSkillCategory(),
                            myPageDTO.getDesiredJob(),
                            member
                    );
                    educationRepository.save(newEducation);
                }
        );
    }
}
