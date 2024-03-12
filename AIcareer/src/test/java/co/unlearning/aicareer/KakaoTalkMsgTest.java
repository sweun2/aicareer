package co.unlearning.aicareer;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KakaoTalkMsgTest {
    @Test
    void 토큰_가져오기() {
        String REST_API_KEY = "567a3bba3ec24bb1cabaadaab32a2325";
        String REDIRECT_URI = "http://localhost:8080/api/user/kakao";
        String url = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+REST_API_KEY+"&redirect_uri="+REDIRECT_URI;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> entity = restTemplate.getForEntity(
                url,
                JsonNode.class
        );
        System.out.println(entity.getBody());
        System.out.println(entity.getHeaders());
    }

}
