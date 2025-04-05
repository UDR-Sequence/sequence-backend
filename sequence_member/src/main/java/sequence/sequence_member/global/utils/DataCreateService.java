package sequence.sequence_member.global.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
import sequence.sequence_member.project.entity.Comment;
import sequence.sequence_member.project.entity.Project;
import sequence.sequence_member.project.entity.ProjectInvitedMember;
import sequence.sequence_member.project.entity.ProjectMember;
import sequence.sequence_member.project.repository.ProjectRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCreateService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Faker faker = new Faker();

    @Transactional
    public CompletableFuture<Void> generateUserBatch(int batchNumber, int batchSize) {
        List<MemberEntity> members = new ArrayList<>();
        long startTime = System.currentTimeMillis();  // 시작 시간 측정
        log.info(batchNumber+" : 유저 데이터 삽입 시작 (멀티스레드)");
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

                // 랜덤 Portfolio 추가 (0~2개)
                int portfolioCount = faker.number().numberBetween(0, 3);
                IntStream.range(0, portfolioCount).forEach(j -> member.getPortfolios().add(
                        new PortfolioEntity(
                                faker.internet().url(),  // 랜덤 포트폴리오 URL
                                member
                        )
                ));

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

    @Async
    @Transactional
    public CompletableFuture<Void> generateProjectBatch(int batchNumber, int batchSize) {
        long startTime = System.currentTimeMillis();
        log.info(batchNumber +" : 프로젝트 데이터 삽입 시작");

        List<MemberEntity> availableMembers = memberRepository.findAll().subList(0, 10000);
        List<Project> projects = new ArrayList<>();

        for(int i=0;i<batchSize;i++) {

            MemberEntity writer = availableMembers.get(faker.number().numberBetween(0, availableMembers.size())); // 랜덤 writer

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

            // 1. 시작일: 오늘 기준 -300일 ~ 오늘 사이
                        LocalDate startDate = LocalDate.now().minusDays(faker.number().numberBetween(0, 301));

            // 2. 종료일: 시작일 기준 +0일 ~ +210일 사이
                        LocalDate endDate = startDate.plusDays(faker.number().numberBetween(0, 211));

            // 3. 문자열로 변환 (yyyy-MM)
                        String startDateStr = startDate.format(formatter);
                        String endDateStr = endDate.format(formatter);

            // 3. 기간 계산
            Period period = Period.calculatePeriod(startDateStr, endDateStr);


            Project project = Project.builder()
                    .title(faker.book().title())
                    .projectName(faker.company().name())
                    .startDate(startDateStr)
                    .endDate(endDateStr)
                    .period(period)
                    .category(faker.options().option(Category.class))
                    .personnel(faker.number().numberBetween(1, 6)) // 1~5명
                    .roles(DataConvertor.listToString(List.of(faker.job().title(), faker.job().title())))
                    .skills(DataConvertor.listToString(getRandomSkills())) // 랜덤 스킬
                    .meetingOption(faker.options().option(MeetingOption.class))
                    .step(faker.options().option(Step.class))
                    .introduce(faker.lorem().paragraph())
                    .article(faker.lorem().paragraph(2))
                    .link(faker.internet().url())
                    .views(0) // 기본값 0
                    .writer(writer)
                    .build();

            //프로젝트 멤버추가(writer 포함 1~5명)
            List<ProjectMember> projectMembers = new ArrayList<>();
            projectMembers.add(new ProjectMember(null, project, writer)); // writer 무조건 포함
            int memberCount = faker.number().numberBetween(1, 5);
            for (int j = 0; j < memberCount; j++) {
                MemberEntity member = availableMembers.get(faker.number().numberBetween(0, availableMembers.size()));
                if (!member.equals(writer)) { // 중복 방지
                    projectMembers.add(new ProjectMember(null, project, member));
                }
            }
            project.setMembers(projectMembers);

            //ProjectInviteMember 추가 (0~5명)
            int invitedCount = faker.number().numberBetween(0, 6);
            List<ProjectInvitedMember> invitedMembers = new ArrayList<>();
            for (int j = 0; j < invitedCount; j++) {
                MemberEntity invited = availableMembers.get(faker.number().numberBetween(0, availableMembers.size()));
                if (!projectMembers.contains(invited)) { // 중복 방지
                    invitedMembers.add(new ProjectInvitedMember(null, project, invited));
                }
            }
            project.setInvitedMembers(invitedMembers);

            //댓글 추가(0~10개, 대댓글 포함)
            int commentCount = faker.number().numberBetween(0, 11);
            List<Comment> comments = new ArrayList<>();
            for (int j = 0; j < commentCount; j++) {
                MemberEntity commentWriter = availableMembers.get(faker.number().numberBetween(0, availableMembers.size()));
                Comment comment = Comment.builder()
                        .project(project)
                        .content(faker.lorem().sentence())
                        .writer(commentWriter)
                        .build();

                // 대댓글 추가 (50% 확률)
                if (faker.bool().bool()) {
                    Comment reply = Comment.builder()
                            .project(project)
                            .content(faker.lorem().sentence())
                            .writer(availableMembers.get(faker.number().numberBetween(0, availableMembers.size())))
                            .parentComment(comment)
                            .build();
                    comments.add(reply);
                }

                comments.add(comment);
            }
            project.setComments(comments);
            projects.add(project);
        }
        projectRepository.saveAll(projects);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.info(batchNumber+" : 프로젝트 데이터 생성 시간 " + totalTime + "ms");
        return CompletableFuture.completedFuture(null);
    }
    /**
     * 랜덤한 스킬 리스트 반환 (2~5개)
     */
    private List<String> getRandomSkills() {
        List<Skill> allSkills = Arrays.asList(Skill.values());
        Collections.shuffle(allSkills);
        return allSkills.subList(0, faker.number().numberBetween(2, 6)).stream()
                .map(Enum::name)
                .toList();
    }
}
