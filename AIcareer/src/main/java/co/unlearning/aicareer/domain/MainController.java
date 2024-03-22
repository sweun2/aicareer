package co.unlearning.aicareer.domain;

import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
@RequestMapping("/")
public class MainController {
    @GetMapping("")
    public void exRedirect3(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("https://aicareer.co.kr");
    }
    @ResponseBody
    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoCallback(@RequestParam(name = "code") String code) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type","application/x-www-form-urlencoded;chartset=utf-8");
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        String REST_API_KEY = "567a3bba3ec24bb1cabaadaab32a2325";
        String REDIRECT_URI = "https://dev.aicareer.co.kr/kakao";
        params.add("grant_type","authorization_code");
        params.add("client_id",REST_API_KEY);
        params.add("redirect_uri",REDIRECT_URI);
        params.add("code",code);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String,String>> kakaoToken = new HttpEntity<>(params,httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoToken,
                String.class
        );
        JSONObject object = new JSONObject(response.getBody());

        String accessToken = object.get("access_token").toString();
        sendKakaoMessage(accessToken);

        return ResponseEntity.ok(response.getBody());
    }
    public void sendKakaoMessage(String accessToken) {
        // RestTemplate에 FormHttpMessageConverter와 StringHttpMessageConverter 추가
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        // JSON 문자열 직접 구성
        String templateObjectJson = "{\"object_type\":\"text\",\"text\":\""+

                "📣AI커리어 오늘의 공고" + "\\n" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREAN)) + "\\n" +
                "⌨ 에이아이커리어 링크\\n" +
                "https://www.aicareer.co.kr\\n" +
                "\\uD83E\\uDDD1\\u200D\\uD83D\\uDCBB 신입/인턴 채용공고—————" + "\\n" +
                "\\uD83D\\uDDA5 " + "companyName " + "[" + "companyType" + "]" + "\\n" +
                "- 직무 : " + "recruitingJob " + "(" + "deadlineType" + ")" + "\\n" +
                "- 요건 : 학력 " + "education" + ", " + "경력 신입/무관" + "\\n" +
                shortenUrlFromNaver("aicareer.co.kr") + "\\n" +
                "\\uD83E\\uDDD1\\u200D\\uD83D\\uDCBB 경력 채용공고—————" + "\\n" +
                "\\uD83D\\uDDA5 " + "companyName " + "[" + "companyType" + "]" + "\\n" +
                "- 직무 : " + "recruitingJob " + "(" + "deadlineType" + ")" + "\\n" +
                "- 요건 : 학력 " + "education" + ", " + "경력 주니어/리더/..." + "\\n" +
                shortenUrlFromNaver("aicareer.co.kr") + "\\n"

                +"\",\"link\":{\"web_url\":\"https://developers.kakao.com\",\"mobile_web_url\":\"https://developers.kakao.com\"},\"button_title\":\"바로 확인\"}";
        // MultiValueMap을 사용하여 form 데이터 구성
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("template_object", templateObjectJson);

        // HttpEntity 구성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        // POST 요청 실행
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/api/talk/memo/default/send", requestEntity, String.class);

        // 응답 출력
        System.out.println(response.getBody());
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
