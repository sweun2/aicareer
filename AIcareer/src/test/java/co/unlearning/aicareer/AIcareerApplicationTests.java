package co.unlearning.aicareer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
@SpringBootTest
class AIcareerApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void LocalDateTime테스트() {
		String testTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		System.out.println("localdatetime to str:");
		System.out.println(testTime);

		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		LocalDateTime testTime2 = LocalDateTime.parse(testTime +":00.000",formatter2);
		System.out.println("str to localdatetime:");
		System.out.println(testTime2);
	}
	@Test
	void statusCode테스트() {
		String url = "https://nvidia.wd5.myworkdayjobs.com/NVIDIAExternalCareerSite/job/Korea-Seoul/Software-Intern---AI-Backend-Infrastructure-GT_JR1975133?source=jobboardlinkedin";
		String userAgent = "Mozilla/5.0 Firefox/26.0"; // 사용하고자 하는 User-Agent 값

		// HttpHeaders 객체 생성 및 User-Agent 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.USER_AGENT, userAgent);

		// RestTemplate 객체 생성
		RestTemplate restTemplate = new RestTemplate();

		// ResponseEntity 객체로 응답 받기
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// 응답 출력
		System.out.println("Response: " + response.getBody());

	}
}
