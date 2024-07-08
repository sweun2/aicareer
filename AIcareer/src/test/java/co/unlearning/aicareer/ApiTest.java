package co.unlearning.aicareer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "server.ssl.enabled=false")
public class ApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetRequestWithCookie() {
        String url = "http://localhost:8080/api/community/posting/2b895e1c-b909-49fd-92db-74b72ebd459b";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.add(HttpHeaders.COOKIE, "_aT=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzd2V1bjNAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MjAyNjM1MTgsImV4cCI6MTcyMDM0OTkxOH0.6ATF84pOTpr3nIwYus4aPrN87oV4kBP0qr0GSqjqNiQ; Path=/;");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // 응답 상태 코드 확인
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // 응답 본문 출력
        System.out.println("Response Body: " + response.getBody());
    }

    @Test
    public void testMultiTimeRequest() throws InterruptedException {
        // 멀티스레드 환경에서 10개의 GET 요청을 동시에 보내는 예시
        CompletableFuture[] futures = IntStream.range(0, 100)
                .mapToObj(i -> CompletableFuture.runAsync(this::testGetRequestWithCookie))
                .toArray(CompletableFuture[]::new);

        // 모든 비동기 작업이 완료될 때까지 기다립니다.
        CompletableFuture.allOf(futures).join();
    }
}
