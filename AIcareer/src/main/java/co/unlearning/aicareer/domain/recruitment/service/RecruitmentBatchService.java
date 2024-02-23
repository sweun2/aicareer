package co.unlearning.aicareer.domain.recruitment.service;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.Image.service.ImageService;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentBatchService {
    private final RecruitmentService recruitmentService;
    private final RecruitmentRepository recruitmentRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    public void printList() {
        getUrlNot2xxRecruitment().forEach(
                (str) -> {
                    System.out.println("url:"+str);
                }
        );
    }
    public void getAllRecruitmentURLNot2xx() {
        List<Recruitment> recruitmentList = recruitmentRepository.findAllByRecruitmentDeadlineTypeIsNot(RecruitmentDeadlineType.EXPIRED);
    }

    public List<String> getUrlNot2xxRecruitment() {
        List<String> urlNot2xx = new ArrayList<>();

        Flux<Map<String, Integer>> flux = Flux.range(0, 10)
                .flatMap(i -> getResponseStatusCode("http://localhost:8080/" + i));

        // 응답 코드가 2xx가 아닌 경우를 필터링하여 urlNot2xx 리스트에 추가
        flux.filter(map -> !HttpStatus.valueOf(map.entrySet().iterator().next().getValue()).is2xxSuccessful())
                .map(map -> map.entrySet().iterator().next().getKey())
                .doOnNext(urlNot2xx::add)
                .then()
                .block(); // Flux의 데이터가 모두 수신될 때까지 대기

        return urlNot2xx;
    }
    public static Mono<Map<String, Integer>> getResponseStatusCode(String url) {
        String userAgent = "Mozilla/5.0 Firefox/26.0"; // 사용하고자 하는 User-Agent 값
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));

        // WebClient 객체 생성 및 User-Agent 설정
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();

        // 비동기로 응답 받기
        return webClient.get()
                .uri(url)
                .exchangeToMono(response -> Mono.just(Map.of(url, response.statusCode().value())));
    }
}
