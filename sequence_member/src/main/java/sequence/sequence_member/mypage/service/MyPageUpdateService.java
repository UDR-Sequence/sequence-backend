package sequence.sequence_member.mypage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.utils.MultipartUtil;
import sequence.sequence_member.member.converter.SkillCategoryConverter;
import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.EducationEntity;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.entity.PortfolioEntity;
import sequence.sequence_member.member.repository.AwardRepository;
import sequence.sequence_member.member.repository.CareerRepository;
import sequence.sequence_member.member.repository.EducationRepository;
import sequence.sequence_member.member.repository.ExperienceRepository;
import sequence.sequence_member.member.repository.PortfolioRepository;
import sequence.sequence_member.mypage.dto.MyPageRequestDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageUpdateService {
    private final String SUFFIX = "auth";
    @Value("${minio.bucketName}")
    private String PROFILE_BUCKET_NAME;

    @Value("${minio.portfolio_bucketName}")
    private String PORTFOLIO_BUCKET_NAME;

    private final String profileImg = "profile";
    private final String portfolioFile = "portfolio";
    private final MultipartUtil multipartUtil;

    private final PortfolioRepository portfolioRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillCategoryConverter skillCategoryConverter;

    /**
     * 주어진 사용자 정보를 바탕으로 마이페이지를 업데이트합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 마이페이지 정보
     * @param profileImg 프로필 이미지 파일
     * @param portfolios  새로운 포트폴리오 파일 목록
     */
    public void updateProfile(
            MemberEntity member, MyPageRequestDTO myPageDTO,
            MultipartFile profileImg, List<MultipartFile> portfolios
    ) {
        updateBasicInfo(member, profileImg, myPageDTO);
        updatePortfolios(member, portfolios);
        updateAwards(member, myPageDTO);
        updateCareers(member, myPageDTO);
        updateExperiences(member, myPageDTO);
        updateEducation(member, myPageDTO);
    }

    /**
     * 사용자 기본 정보를 업데이트합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 기본 정보
     */
    private void updateBasicInfo(MemberEntity member, MultipartFile profileImg, MyPageRequestDTO myPageDTO) {
        if (profileImg != null && !profileImg.isEmpty()) {
            log.info("profileImg: originalFilename = {}, size = {}", profileImg.getOriginalFilename(), profileImg.getSize());
            try {
                // 새 파일 이름 결정
                String profileImageName = multipartUtil.determineFileName(
                        profileImg, member.getUsername(), SUFFIX, PROFILE_BUCKET_NAME, "profile"
                );

                // 프로필 이미지명 업데이트
                member.setProfileImg(profileImageName);
            } catch (Exception e) {
                String errorMessage = "프로필 이미지 처리 중 오류가 발생했습니다: " + e.getMessage();
                log.info(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } else {
            log.info("profileImg is null or empty.");
        }

        member.setName(myPageDTO.getName());
        member.setBirth(myPageDTO.getBirth());
        member.setGender(myPageDTO.getGender());
        member.setAddress(myPageDTO.getAddress());
        member.setPhone(myPageDTO.getPhone());
        member.setIntroduction(myPageDTO.getIntroduction());
        member.setNickname(myPageDTO.getNickname());
    }

    /**
     * 사용자의 포트폴리오를 업데이트합니다.
     * 기존 포트폴리오를 삭제하고 새로운 포트폴리오를 추가합니다.
     *
     * @param member      업데이트할 사용자 엔티티
     * @param portfolios  새로운 포트폴리오 파일 목록
     */
    private void updatePortfolios(
            MemberEntity member, List<MultipartFile> portfolios
    ) {
        if (portfolios != null && !portfolios.isEmpty()) {
            log.info("Number of portfolios received: {}", portfolios.size());
            for (MultipartFile portfolio : portfolios) {
                log.info("Portfolio file: originalFilename = {}, size = {}", portfolio.getOriginalFilename(), portfolio.getSize());
            }
        } else {
            log.info("Portfolios are null or empty.");
        }

        try {
            // 기존 포트폴리오 삭제
            List<PortfolioEntity> existPortfolios = member.getPortfolios();
            existPortfolios.forEach(portfolio -> portfolio.softDelete(member.getUsername()));

            // 새로운 포트폴리오 파일명 리스트
            List<String> portfolioNames = new ArrayList<>();

            // 포트폴리오가 존재하는 경우 처리
            if (portfolios != null && !portfolios.isEmpty()) {
                for (MultipartFile portfolio : portfolios) {
                    try {
                        // 포트폴리오 파일 이름 결정
                        String portfolioName = multipartUtil.determineFileName(
                                portfolio, member.getUsername(), SUFFIX, PORTFOLIO_BUCKET_NAME, portfolioFile
                        );
                        portfolioNames.add(portfolioName);
                    } catch (Exception e) {
                        // 포트폴리오 파일 처리 오류가 발생한 경우
                        String errorMessage = "포트폴리오 파일 처리 중 오류가 발생했습니다: " + e.getMessage();
                        // 로그에 오류 메시지 출력
                        log.info(errorMessage);
                        throw new RuntimeException(errorMessage);  // 예외 던져서 외부로 전달
                    }
                }
            }

            // 새로운 포트폴리오 저장
            if (!portfolioNames.isEmpty()) {
                try {
                    List<PortfolioEntity> portfolioEntities = PortfolioEntity.toPortfolioEntity(portfolioNames, member);
                    portfolioRepository.saveAll(portfolioEntities);
                } catch (Exception e) {
                    // 포트폴리오 엔티티 저장 시 예외 처리
                    String errorMessage = "포트폴리오 엔티티 저장 중 오류가 발생했습니다: " + e.getMessage();
                    log.info(errorMessage);
                    throw new RuntimeException(errorMessage);  // 예외 던져서 외부로 전달
                }
            }
        } catch (Exception e) {
            // 전체 로직에서 발생한 예외 처리
            String errorMessage = "포트폴리오 업데이트 중 오류가 발생했습니다: " + e.getMessage();
            log.info(errorMessage);
            throw new RuntimeException(errorMessage);  // 예외 던져서 외부로 전달
        }
    }

    /**
     * 사용자의 수상 경력을 업데이트합니다.
     * 기존 수상 경력을 삭제하고 새로운 경력을 추가합니다.
     *
     * @param member 업데이트할 사용자 엔티티
     * @param myPageDTO 사용자가 제공한 수상 경력 정보
     */
    private void updateAwards(MemberEntity member, MyPageRequestDTO myPageDTO) {
        List<AwardEntity> existingAwards = awardRepository.findByMemberAndIsDeletedFalse(member);
        List<AwardEntity> newAwards = myPageDTO.getAwards().stream()
                .map(dto -> new AwardEntity(dto.getAwardType(), dto.getOrganizer(), dto.getAwardName(), dto.getAwardDuration(), member))
                .collect(Collectors.toList());

        existingAwards.forEach(award -> award.softDelete(member.getUsername()));
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
        List<CareerEntity> existingCareers = careerRepository.findByMemberAndIsDeletedFalse(member);  // 기존 경력 조회
        List<CareerEntity> newCareers = myPageDTO.getCareers().stream()               // 새로운 경력으로 업데이트
                .map(dto -> new CareerEntity(dto.getCompanyName(), dto.getStartDate(), dto.getEndDate(), dto.getCareerDescription(), member))
                .collect(Collectors.toList());

        existingCareers.forEach(career -> career.softDelete(member.getUsername()));
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
        List<ExperienceEntity> existingExperiences = experienceRepository.findByMemberAndIsDeletedFalse(member);  // 기존 경험 조회
        List<ExperienceEntity> newExperiences = myPageDTO.getExperiences().stream()               // 새로운 경험으로 업데이트
                .map(dto -> new ExperienceEntity(dto.getExperienceType(), dto.getExperienceName(), dto.getStartDate(), dto.getEndDate(), dto.getExperienceDescription(), member))
                .collect(Collectors.toList());

        existingExperiences.forEach(experience -> experience.softDelete(member.getUsername()));
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
                        myPageDTO.getEntranceYear(),
                        myPageDTO.getGraduationYear(),
                        myPageDTO.getDegree(),
                        skillCategoryConverter.convertToSkillEnum(myPageDTO.getSkillCategory()),
                        myPageDTO.getDesiredJob()
                ),
                () -> {
                    EducationEntity newEducation = new EducationEntity(
                            myPageDTO.getSchoolName(),
                            myPageDTO.getMajor(),
                            myPageDTO.getGrade(),
                            myPageDTO.getEntranceYear(),
                            myPageDTO.getGraduationYear(),
                            myPageDTO.getDegree(),
                            skillCategoryConverter.convertToSkillEnum(myPageDTO.getSkillCategory()),
                            myPageDTO.getDesiredJob(),
                            member
                    );
                    educationRepository.save(newEducation);
                }
        );
    }
}
