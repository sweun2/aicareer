package co.unlearning.aicareer.domain.job.recruitmentbatch;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    public String requestToOpenAI(String title, String reqMsg) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + apiKey);

            // 메시지를 청크로 분할하는 함수
            List<String> chunks = splitIntoChunks(reqMsg); // 50,000 토큰에 해당하는 크기로 분할

            StringBuilder finalResponse = new StringBuilder();

            for (String chunk : chunks) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "gpt-3.5-turbo");

                Map<String, String> userMessage1 = new HashMap<>();
                userMessage1.put("role", "user");
                userMessage1.put("content", "너가 글을 보고 공고를 한국어 text로 요약해줘. 제목은 " + title + "이니까 이를 참고해서 해당 공고만 작성해줘. 그리고 회사명은 한개만 존재해야되. 정리되지 않은 채용공고는 다음과 같아. 회사는 에이아이 커리어가 아닌 다른 하나만 적어줘. 현재 이미지와 글이 섞인 상태야.  글은 다음과 같아." + chunk);

                Map<String, String> userMessage2 = new HashMap<>();
                userMessage2.put("role", "user");
                userMessage2.put("content", """
                    public static class RecruitmentPost {
                    @Schema(description = "메인 이미지 url")
                    private String mainImage;
                    @Schema(description = "서브 이미지 url")
                    private List<String> subImage;
                    @Schema(description = "회사 주소")
                    private String companyAddress;
                    @Schema(description = "회사명")
                    private String companyName;
                    @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC","FOREIGN","ETC"})
                    private String companyType;
                    @Schema(description = "모집 직무", allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC","PM_PO","CONSULTANT","SOFTWARE_ENGINEER","SALES","OPERATION"})
                    @NotNull
                    private List<String> recruitingJobNames;
                    @Schema(description = "채용 유형", allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
                    @NotNull
                    private List<String> recruitmentTypeNames;
                    @Schema(description = "학력 조건", allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR", "IRRELEVANCE"})
                    @NotNull
                    private List<String> educations;
                    @Schema(description = "요구 경력"  , allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER","IRRELEVANCE"})
                    private List<String> careers;
                    @Schema(description = "모집 시작일, 일자/시간 사이 빈칸 필요", defaultValue = "작성 시 시간", allowableValues = {"yyyy-MM-dd HH:mm"})
                    private String recruitmentStartDate; // 모집 시작일
                    @Schema(description = "모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
                    private RecruitmentDeadLine recruitmentDeadline; //모집 마감일
                    @Schema(description = "모집 공고 링크")
                    private String recruitmentAnnouncementLink; //모집 공고 링크
                    @Schema(description = "모집 지역", allowableValues = {"SEOUL", "GANGNAM","MAPO","GURO_GARSAN_GAME","BUNDANG_PANGYO","ETC"})
                    private String recruitmentAddress; //지역
                    @Schema(description = "제목")
                    private String title; //title
                    @Schema(description = "내용")
                    private String content; //내용
                    @Schema(description = "내부 타입", allowableValues = {"MARKDOWN,HTML"})
                    private String textType;
                    }
                    만들어진 공고를 위 DTO에 맞춰서 json 객체로 만들어서 반환해줘. content에는 공고 내용이 들어가면 되.
                    """);

                requestBody.put("messages", List.of(userMessage1, userMessage2));

                String requestBodyJson = objectMapper.writeValueAsString(requestBody);

                HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    finalResponse.append(response.getBody());
                } else {
                    log.error("Error in response from OpenAI: {}", response.getStatusCode());
                    return "Failed to extract text";
                }
            }

            return finalResponse.toString();
        } catch (Exception e) {
            log.error("Failed to request OpenAI: ", e);
            return "Failed to extract text";
        }
    }

    // 요청 메시지를 청크로 분할하는 함수
    private List<String> splitIntoChunks(String text) {
        int maxLength = 30000;
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += maxLength) {
            chunks.add(text.substring(i, Math.min(length, i + maxLength)));
        }
        return chunks;
    }

}
