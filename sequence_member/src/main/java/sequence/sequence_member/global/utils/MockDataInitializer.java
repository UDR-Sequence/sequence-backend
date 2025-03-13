package sequence.sequence_member.global.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class MockDataInitializer implements ApplicationRunner {

    private final DataCreateService dataCreateService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int TOTAL_USERS = 100000;  // 100만 명
    private static final int BATCH_SIZE = 10000;      // 배치 저장 크기
    private static final int THREAD_COUNT = 10;       // 사용할 스레드 개수

    @Override
    @Transactional
    public void run(ApplicationArguments args){
        System.out.println("데이터 삽입 시작");
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for(int i=0;i< TOTAL_USERS / BATCH_SIZE; i++){
            final int batchNumber = i;
            executorService.submit(()-> dataCreateService.insertMemberBatch(batchNumber));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // 모든 작업 완료될 때까지 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }




}
