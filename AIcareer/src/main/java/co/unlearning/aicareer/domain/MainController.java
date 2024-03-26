package co.unlearning.aicareer.domain;

import co.unlearning.aicareer.domain.job.career.Career;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.kakaotalk.service.KaKaoTalkService;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class MainController {
    private final RecruitmentService recruitmentService;
    private final KaKaoTalkService kaKaoTalkService;
    @Value("${back-url}")
    private String backUrl;
    @GetMapping("")
    public void exRedirect3(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("https://aicareer.co.kr");
    }
    @ResponseBody
    @GetMapping("/kakao")
    public ResponseEntity<String> kakaoCallback(@RequestParam(name = "code") String code) throws Exception {
        log.info(code);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type","application/x-www-form-urlencoded;chartset=utf-8");
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        String REST_API_KEY = "567a3bba3ec24bb1cabaadaab32a2325";
        String REDIRECT_URI = backUrl+"/kakao";
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
        kaKaoTalkService.sendKakaoMessage(accessToken);

        return ResponseEntity.ok(response.getBody());
    }
}
