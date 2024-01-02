package co.unlearning.aicareer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}
