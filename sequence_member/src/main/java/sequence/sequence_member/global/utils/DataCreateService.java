package sequence.sequence_member.global.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.global.enums.enums.*;
import sequence.sequence_member.member.entity.*;
import sequence.sequence_member.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCreateService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Faker faker = new Faker();

    @Async
    @Transactional
    public CompletableFuture<Void> generatUserBatch(int batchNumber, int batchSize) {
        List<MemberEntity> members = new ArrayList<>();
        long startTime = System.currentTimeMillis();  // 시작 시간 측정
        log.info("데이터 삽입 시작 (멀티스레드)");
        String password = passwordEncoder.encode("password");
        try {
            for (int i = 0; i < batchSize; i++) {
                MemberEntity member = new MemberEntity();
                member.setUsername(batchNumber + "_" + i + "_username");
                member.setPassword(password);
                member.setName(batchNumber + "_" + i + "_" + faker.name().name());
                member.setBirth(Date.from(faker.date().birthday(18, 50).toInstant().atZone(ZoneId.systemDefault()).toInstant()));
                member.setGender(faker.bool().bool() ? MemberEntity.Gender.M : MemberEntity.Gender.F);
                member.setAddress(faker.address().fullAddress());
                member.setPhone(faker.numerify("010-####-####"));  // 한국식 번호
                member.setEmail(batchNumber + "_" + i + "_" + faker.internet().emailAddress());
                member.setIntroduction(faker.lorem().sentence());
                member.setPortfolio(faker.internet().url());
                member.setNickname(batchNumber + "_" + i + "_" + faker.funnyName().name());
                member.setSchoolName(faker.educator().university());
                member.setProfileImg(faker.internet().avatar());
                member.setDeleted(false);

                // EducationEntity 생성
                EducationEntity education = new EducationEntity(
                        faker.educator().university(),
                        faker.educator().course(),
                        faker.number().numberBetween(1, 6) + "학년",
                        Date.from(faker.date().past(2000, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(faker.date().future(1000, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toInstant()),
                        faker.options().option(Degree.class),
                        List.of(faker.options().option(Skill.class)),
                        List.of(faker.options().option(ProjectRole.class)),
                        member
                );
                member.setEducation(education);

                // 랜덤 Award 추가 (0~5개)
                IntStream.range(0, faker.number().numberBetween(0, 6)).forEach(j -> member.getAwards().add(
                        new AwardEntity(
                                faker.options().option(AwardType.class),
                                faker.company().name(),
                                faker.book().title(),
                                Date.from(faker.date().past(1000, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toInstant()),
                                member
                        )
                ));

                // 랜덤 Career 추가 (0~5개)
                IntStream.range(0, faker.number().numberBetween(0, 6)).forEach(j -> {
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
                IntStream.range(0, faker.number().numberBetween(0, 6)).forEach(j -> {
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
            memberRepository.saveAll(members);
            memberRepository.flush();
            System.out.println("Batch " + batchNumber + " 저장 완료");
        } catch (Exception e) {
            System.err.println("Batch " + batchNumber + " 생성 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();  // 종료 시간 측정
        long totalTime = endTime - startTime;  // 총 시간 계산

        log.info("데이터 삽입 완료");
        log.info("전체 데이터 생성 시간: " + totalTime + "ms");

        return CompletableFuture.completedFuture(null);
    }
}
