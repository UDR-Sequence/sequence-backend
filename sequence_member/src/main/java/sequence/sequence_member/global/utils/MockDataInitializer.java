//package sequence.sequence_member.global.utils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.stereotype.Component;
//import sequence.sequence_member.member.repository.MemberRepository;
//
//@Component
//@EnableAsync
//@Slf4j
//@RequiredArgsConstructor
//public class MockDataInitializer implements ApplicationRunner {
//
//    private final MemberRepository memberRepository;
//    private final DataCreateService dataCreateService;
//    private static final int TOTAL_USERS = 10000;
//    private static final int USER_BATCH_SIZE = 1000;
//    private static final int THREAD_POOL_SIZE = 10;  // 병렬 실행할 스레드 개수
//    private static final int PROJECT_TOTAL = 10000;
//    private static final int PROJECT_BATCH_SIZE = 1000;
//    @Override
//    public void run(ApplicationArguments args) {
//
//        // 이미 목데이터 생성되었는지를 판단함.
//        if (memberRepository.findByUsernameAndIsDeletedFalse("1_0_username").isPresent()) {
//            return;
//        }
//
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        long startTime = System.currentTimeMillis();
//
//        // 1. 유저 데이터 생성
//        long startUserTime = System.currentTimeMillis();
//        for (int batchNumber = 0; batchNumber < TOTAL_USERS / USER_BATCH_SIZE; batchNumber++) {
//            int finalBatchNumber = batchNumber;
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
//                    dataCreateService.generateUserBatch(finalBatchNumber, USER_BATCH_SIZE), executorService);
//            futures.add(future);
//        }
//
//        // 모든 비동기 작업이 끝날 때까지 기다림
//        CompletableFuture<Void> allUsersDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        allUsersDone.join();  // 모든 작업이 끝날 때까지 대기
//        long endUserTime = System.currentTimeMillis();
//        long totalUserTime = endUserTime - startUserTime;  // 총 시간 계산
//        log.info("유저 전체 목 데이터 삽입 완료");
//        log.info("유저 전체 목 데이터 생성 시간: " + totalUserTime + "ms");
//
//        futures.clear();
//
//
//        log.info("프로젝트 데이터 생성 시작");
//        long startProjectTime = System.currentTimeMillis();
//        for (int batchNumber = 0; batchNumber < PROJECT_TOTAL / PROJECT_BATCH_SIZE; batchNumber++) {
//            int finalBatchNumber = batchNumber;
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
//                    dataCreateService.generateProjectBatch(finalBatchNumber, PROJECT_BATCH_SIZE), executorService);
//            futures.add(future);
//        }
//
//        CompletableFuture<Void> allProjectDataDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        allProjectDataDone.join(); // 모든 프로젝트 데이터가 완료될 때까지 대기
//        long endProjectTime = System.currentTimeMillis();
//        long totalProjectTime = endProjectTime - startProjectTime;
//        log.info("프로젝트 전체 목 데이터 삽입 완료");
//        log.info("프로젝트 전체 목 데이터 생성 시간: " + totalProjectTime + "ms");
//
//        log.info("모든 데이터 생성 완료!");
//        long endTime = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//        log.info("전체 목 데이터 삽입 완료");
//        log.info("전체 목 데이터 소요 시간 : " + totalTime + "ms");
//
//        executorService.shutdown();
//
//    }
//
//}
