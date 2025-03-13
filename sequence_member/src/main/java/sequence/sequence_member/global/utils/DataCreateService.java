package sequence.sequence_member.global.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.enums.enums.AwardType;
import sequence.sequence_member.global.enums.enums.Degree;
import sequence.sequence_member.global.enums.enums.ExperienceType;
import sequence.sequence_member.global.enums.enums.ProjectRole;
import sequence.sequence_member.global.enums.enums.Skill;
import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.EducationEntity;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class DataCreateService {

    private static final int BATCH_SIZE = 10000;      // 배치 저장 크기
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Transactional
    public void insertMemberBatch(int batchNumber){
        Faker faker = new Faker();
        List<MemberEntity> members = new ArrayList<>();

        for (int i=0; i < BATCH_SIZE ; i++){
            MemberEntity member = new MemberEntity();
            member.setUsername(faker.name().username() + batchNumber + i);
            member.setPassword(passwordEncoder.encode("password"));
            member.setName(faker.name().fullName());
            member.setBirth(Date.from(faker.date().birthday(18,50).toInstant().atZone(ZoneId.systemDefault()).toInstant()));
            member.setGender(faker.bool().bool() ? MemberEntity.Gender.M : MemberEntity.Gender.F);
            member.setAddress(faker.address().fullAddress());
            member.setPhone(faker.phoneNumber().phoneNumber());
            member.setEmail(faker.internet().emailAddress());
            member.setIntroduction(faker.lorem().sentence());
            member.setPortfolio(faker.internet().url());
            member.setNickname(faker.funnyName().name());
            member.setSchoolName(faker.educator().university());
            member.setProfileImg(faker.internet().avatar());
            member.setDeleted(false);

            //EducationEntity는 무조건 생성
            EducationEntity education = new EducationEntity(
                    faker.educator().university(),
                    faker.educator().course(),
                    String.valueOf(faker.number().numberBetween(1, 6)) + "학년",
                    Date.from(faker.date().past(2000, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(faker.date().future(1000, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toInstant()),
                    faker.options().option(Degree.class),
                    List.of(faker.options().option(Skill.class)),
                    List.of(faker.options().option(ProjectRole.class)),
                    member
            );
            member.setEducation(education);

            // 랜덤 Award 추가 (0~5개)
            int awardCount = faker.number().numberBetween(0, 6);
            IntStream.range(0, awardCount).forEach(j -> member.getAwards().add(
                    new AwardEntity(
                            faker.options().option(AwardType.class),
                            faker.company().name(),
                            faker.book().title(),
                            Date.from(faker.date().past(1000, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toInstant()),
                            member
                    )
            ));

            // 랜덤 Career 추가 (0~5개)
            int careerCount = faker.number().numberBetween(0, 6);
            IntStream.range(0, careerCount).forEach(j -> {
                LocalDate startDate = LocalDate.now().minusYears(faker.number().numberBetween(1, 5));
                LocalDate endDate = startDate.plusYears(faker.number().numberBetween(1, 3));
                member.getCareers().add(new CareerEntity(
                        faker.company().name(),
                        startDate,
                        endDate,
                        faker.lorem().sentence(),
                        member
                ));
            });

            // 랜덤 Experience 추가 (0~5개)
            int experienceCount = faker.number().numberBetween(0, 6);
            IntStream.range(0, experienceCount).forEach(j -> {
                LocalDate startDate = LocalDate.now().minusYears(faker.number().numberBetween(1, 5));
                LocalDate endDate = startDate.plusYears(faker.number().numberBetween(1, 3));
                member.getExperiences().add(new ExperienceEntity(
                        faker.options().option(ExperienceType.class),
                        faker.job().title(),
                        startDate,
                        endDate,
                        faker.lorem().sentence(),
                        member
                ));
            });
            members.add(member);

        }
        memberRepository.saveAll(members); // Cascade 적용됨
        System.out.println("Batch " + batchNumber + " inserted.");
    }
}
