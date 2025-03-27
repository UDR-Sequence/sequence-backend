package sequence.sequence_member.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sequence.sequence_member.global.response.ApiResponseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 실제 서버처럼 랜덤 포트로 애플리케이션을 띄움
@AutoConfigureMockMvc
public class MemberControllerTest {

    @LocalServerPort // 랜덤으로 생성된 서버 포트를 주입받음
    private int port;

    private String baseUrl;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 대상 API의 base URL 설정
        baseUrl = "http://localhost:" + port + "/api/users";
        restTemplate = new RestTemplate(); // HTTP 요청을 위한 RestTemplate 인스턴스 생성
    }

    @Test
    void checkUsername_사용가능한아이디_성공() {
        // [Given] 존재하지 않는 아이디 설정
        String username = "uniqueUser123";
        String url = baseUrl + "/check_username?username=" + username;

        // [When] GET 요청 전송 (쿼리 파라미터로 username 포함)
        ResponseEntity<ApiResponseData<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null, // 요청 본문 없음
                new ParameterizedTypeReference<ApiResponseData<String>>() {} // 응답 타입 지정 (제네릭 대응)
        );

        // [Then] 응답 상태와 메시지 검증
        assertEquals(HttpStatus.OK, response.getStatusCode()); // 상태 코드 200
        assertNotNull(response.getBody()); // 응답 본문 존재 여부 확인
        assertEquals("사용가능한 아이디 입니다.", response.getBody().getData()); // 메시지 일치 여부 확인
    }
}
