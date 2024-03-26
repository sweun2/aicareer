package co.unlearning.aicareer.domain.kakaotalk.service;

import co.unlearning.aicareer.domain.job.career.Career;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KaKaoTalkService {
    @Value("${front-url}")
    private String frontUrl;
    private final RecruitmentService recruitmentService;
    public void sendKakaoMessage(String accessToken) {
        // RestTemplate에 FormHttpMessageConverter와 StringHttpMessageConverter 추가
        log.info("sendkakaotalk");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);
        List<Career.AnnualLeave> newComerList = List.of(Career.AnnualLeave.NEW_COMER,Career.AnnualLeave.IRRELEVANCE);
        List<Recruitment> newComerSet = recruitmentService.getTodayRecruitmentsWithCareer(newComerList);
        List<Career.AnnualLeave> expertList = List.of(Career.AnnualLeave.JUNIOR,Career.AnnualLeave.MIDDLE, Career.AnnualLeave.LEADER, Career.AnnualLeave.SENIOR);
        List<Recruitment> expertSet = recruitmentService.getTodayRecruitmentsWithCareer(expertList);
        String newComerMsg = "";
        String expertMsg = "";
        for(Recruitment recruitment : newComerSet) {
            log.info(recruitment.getUid());
            newComerMsg += "\\uD83D\\uDDA5 " + recruitment.getCompany().getCompanyName() + " [" + recruitment.getCompany().getCompanyType().getCompanyTypeName().getKoreanName() + "]" + "\\n" +
                    "- 직무 : " + recruitment.getRecruitingJobSet().stream().map(job->job.getRecruitJobName().getKoreanName()).collect(Collectors.joining(", ")) + " (" + recruitment.getRecruitmentDeadlineType().getKoreanName() + ")" + "\\n" +
                    "- 요건 : 학력 " + recruitment.getEducationSet().stream().map(edu -> edu.getDegree().getKoreanName()).collect(Collectors.joining(",")) +
                    ", " +
                    "경력 " + recruitment.getCareerSet().stream().map(car -> car.getAnnualLeave().getKoreanName()).collect(Collectors.joining(", ")) + "\\n" +
                    shortenUrlFromNaver(frontUrl+"/recruitment/"+recruitment.getUid()) + "\\n" + "\\n";
        }
        String templateObjectJson1 = "{\"object_type\":\"text\",\"text\":\"" +
                "\\uD83D\\uDEA8 AI커리어 오늘의 공고" + "\\n" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN)) + "\\n" + "\\n" +
                "⌨ 맞춤 채용공고 메일 등록하기\\n" +
                "https://www.aicareer.co.kr/?milp=true" + "\\n" + "\\n" +
                "\\uD83E\\uDDD1\\u200D\\uD83D\\uDCBB 신입/인턴 채용공고—————" + "\\n" + "\\n" +
                newComerMsg +
                "\",\"link\":{\"web_url\":\"https://developers.kakao.com\",\"mobile_web_url\":\"https://developers.kakao.com\"},\"button_title\":\"바로 확인\"}";

        // MultiValueMap을 사용하여 form 데이터 구성
        MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
        map1.add("template_object", templateObjectJson1);
        HttpEntity<MultiValueMap<String, String>> requestEntity1 = new HttpEntity<>(map1, headers);
        ResponseEntity<String> response1 = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/api/talk/memo/default/send", requestEntity1, String.class);




        for(Recruitment recruitment : expertSet) {
            log.info(recruitment.getUid());
            expertMsg += "\\uD83D\\uDDA5 " + recruitment.getCompany().getCompanyName() + " [" + recruitment.getCompany().getCompanyType().getCompanyTypeName().getKoreanName() + "]" + "\\n" +
                    "- 직무 : " + recruitment.getRecruitingJobSet().stream().map(job->job.getRecruitJobName().getKoreanName()).collect(Collectors.joining(", ")) + " (" + recruitment.getRecruitmentDeadlineType().getKoreanName() + ")" + "\\n" +
                    "- 요건 : 학력 " + recruitment.getEducationSet().stream().map(edu -> edu.getDegree().getKoreanName()).collect(Collectors.joining(",")) +
                    ", " +
                    "경력 " + recruitment.getCareerSet().stream().map(job -> job.getAnnualLeave().getKoreanName()).collect(Collectors.joining(", ")) + "\\n" +
                    shortenUrlFromNaver(frontUrl+"/recruitment/"+recruitment.getUid()) + "\\n" + "\\n" ;
        }
        String templateObjectJson2 = "{\"object_type\":\"text\",\"text\":\""+

                "\\uD83E\\uDDD1\\u200D\\uD83D\\uDCBB 경력 채용공고—————" + "\\n" + "\\n" +
                expertMsg +
                "\",\"link\":{\"web_url\":\"https://developers.kakao.com\",\"mobile_web_url\":\"https://developers.kakao.com\"},\"button_title\":\"바로 확인\"}";

        MultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
        map2.add("template_object", templateObjectJson2);

        // HttpEntity 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity2 = new HttpEntity<>(map2, headers);

        // POST 요청 실행
        ResponseEntity<String> response2 = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/api/talk/memo/default/send", requestEntity2, String.class);

        System.out.println(response1.getBody());
        System.out.println(response2.getBody());
    }
    public String shortenUrlFromNaver(String url) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        String clientId = "aY6ehque07j5CjYTBIcX";
        String clientSecret = "73CHBBQ3sD";
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("X-Naver-Client-Id",clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("url", url);

        // UriComponentsBuilder를 사용하여 URL 인코딩 처리
        URI uri = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/util/shorturl")
                .queryParams(map)
                .build()
                .encode()
                .toUri();

        // HttpEntity 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        // POST 요청 실행
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONObject result = (JSONObject) jsonObject.get("result");
        return  result.get("url").toString();
    }
    public String makeRecruitmentKAKAOTalkMsg() {
        String msg = "";
        msg = "📣AI커리어 오늘의 공고" + "\n";
        msg += LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN)) + "\n";
        msg += "⌨ 에이아이커리어 링크\n" +
                "https://www.aicareer.co.kr\n";
        msg += "신입/인턴 채용공고—————" + "\n";
        msg += " " + "companyName " + "[" + "companyType" + "]" + "\n";
        msg += "- 직무 : " + "recruitingJob " + "(" + "deadlineType" + ")" + "\n";
        msg += "- 요건 : 학력 " + "education" + ", " + "경력 신입/무관" + "\n";
        msg += shortenUrlFromNaver("aicareer.co.kr") + "\n";
        msg += "경력 채용공고—————" + "\n";
        msg += " " + "companyName " + "[" + "companyType" + "]" + "\n";
        msg += "- 직무 : " + "recruitingJob " + "(" + "deadlineType" + ")" + "\n";
        msg += "- 요건 : 학력 " + "education" + ", " + "경력 주니어/리더/..." + "\n";
        msg += shortenUrlFromNaver("aicareer.co.kr") + "\n";
        return msg;
    }
}
