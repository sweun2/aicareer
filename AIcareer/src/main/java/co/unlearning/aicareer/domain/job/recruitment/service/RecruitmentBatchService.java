package co.unlearning.aicareer.domain.job.recruitment.service;

import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentBatch;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentBatchService {
    private final RecruitmentService recruitmentService;
    public void printList() {
        getUrlNot2xxRecruitment().forEach(
                (str) -> {
                    System.out.println("url:"+str);
                }
        );
    }
/*    public void getAllRecruitmentURLNot2xx() {
        List<Recruitment> urlNot2xx = getUrlNot2xxRecruitment();
        if()
    }*/

    public List<Recruitment> getUrlNot2xxRecruitment() {
        List<Recruitment> urlNot2xx = new ArrayList<>();
        List<Recruitment> recruitmentList = recruitmentService.findAllNotInRecruitmentDeadlineTypes(Arrays.asList(RecruitmentDeadlineType.DUE_DATE, RecruitmentDeadlineType.EXPIRED));

        Flux<Map<Recruitment, Integer>> flux = Flux.fromIterable(recruitmentList)
                .flatMap(this::getResponseStatusCodeFromRecruitment);

        // 응답 코드가 2xx가 아닌 경우를 필터링하여 urlNot2xx 리스트에 추가
        flux.filter(map -> !HttpStatus.valueOf(map.entrySet().iterator().next().getValue()).is2xxSuccessful())
                .map(map -> map.entrySet().iterator().next().getKey())
                .doOnNext(urlNot2xx::add)
                .then()
                .block(); // Flux의 데이터가 모두 수신될 때까지 대기

        return urlNot2xx;
    }
    public Mono<Map<Recruitment, Integer>> getResponseStatusCodeFromRecruitment(Recruitment recruitment) {
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
                .uri(recruitment.getRecruitmentAnnouncementLink())
                .exchangeToMono(response -> Mono.just(Map.of(recruitment, response.statusCode().value())));
    }
}
