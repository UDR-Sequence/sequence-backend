package sequence.sequence_member.global.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class MockDataInitializer implements ApplicationRunner {

    private final DataCreateService dataCreateService;
    private static final int TOTAL_USERS = 100000;
    private static final int BATCH_SIZE = 10000;
    private static final int THREAD_POOL_SIZE = 10;  // 병렬 실행할 스레드 개수

    @Override
    public void run(ApplicationArguments args) {


        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int batchNumber = 0; batchNumber < TOTAL_USERS / BATCH_SIZE; batchNumber++) {
            int finalBatchNumber = batchNumber;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                    dataCreateService.generateBatch(finalBatchNumber, BATCH_SIZE), executorService);
            futures.add(future);
        }

        // 모든 비동기 작업이 끝날 때까지 기다림
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();  // 모든 작업이 끝날 때까지 대기

        executorService.shutdown();
    }
}
