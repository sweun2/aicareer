package co.unlearning.aicareer.domain.job.recruitmentbatch;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class GptService {
    public String requestToOpenAI(String reqMsg) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String key = "sk-proj-CpsoIFo0innCDzha3lvTT3BlbkFJl31h9S0EhPnhW7NIleQ8";
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + key);

        String requestBody = String.format("""
            {
                "model":"gpt-3.5-turbo",
                "messages":
                [
                     {
                         "role": "user",
                         "content": "최대한 원본의 text를 살려서 채용공고를 마크다운 형식으로 작성해줘, 회사명은 한개만 존재해야되. 정리되지 않은 채용공고는 다음과 같아. %s"
                     }
                ]
            }
            """, reqMsg);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }
}
