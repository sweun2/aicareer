package co.unlearning.aicareer.domain.job.recruitmentbatch;

import co.unlearning.aicareer.domain.job.recruitment.dto.RecruitmentRequirementDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
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

    private static final int RATE_LIMIT_WAIT_TIME_MS = 60000; // 1분 대기 시간

    public RecruitmentRequirementDto.RecruitmentPost requestToOpenAI(String title, String reqMsg, String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String gptEndpoint = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + apiKey);

            // HTML 태그를 제거하고 줄바꿈을 정리하는 함수 호출
            String cleanedReqMsg = cleanHtmlTags(reqMsg);

            // 메시지를 청크로 분할하는 함수
            List<String> chunks = splitIntoChunks(cleanedReqMsg); // 25,000 토큰에 해당하는 크기로 분할

            StringBuilder finalResponse = new StringBuilder();

            for (String chunk : chunks) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "gpt-3.5-turbo");

                Map<String, String> userMessage1 = new HashMap<>();
                userMessage1.put("role", "user");
                userMessage1.put("content", "너가 요청들을 보고 공고를 한국어 text로 요약해줘. 제목은 " + title + "이니까 이를 참고해서 해당 공고만 작성해줘. 그리고 회사명은 한개만 존재해야되. 정리되지 않은 채용공고는 다음과 같아. 회사는 에이아이 커리어가 아닌 다른 하나만 적어줘. 현재 이미지와 글이 섞인 상태야. 글은 다음과 같아." + chunk);

                String userMessage2Content = """
                        @Schema(description = "메인 이미지 url")
                        private String mainImage;
                        @Schema(description = "서브 이미지 url")
                        private List<String> subImage;
                        @Schema(description = "회사 주소")
                        private String companyAddress;
                        @Schema(description = "회사명")
                        private String companyName;
                        @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC","FOREIGN","ETC","PUBLIC_INSTITUTION", "SMALL_MARKET"})
                        private String companyType;
                        @Schema(description = "모집 직무",allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC","PM_PO","BUSINESS","SOFTWARE_ENGINEER","SALES","OPERATION"})
                        @NotNull
                        private List<String> recruitingJobNames;
                        @Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
                        @NotNull
                        private List<String> recruitmentTypeNames;
                        @Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR", "IRRELEVANCE"})
                        @NotNull
                        private List<String> educations;
                        @Schema(description = "요구 경력", allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER","IRRELEVANCE"})
                        private List<String> careers;
                        @Schema(description = "모집 시작일, 일자/시간 사이 빈칸 필요", defaultValue = "작성 시 시간", allowableValues = {"yyyy-MM-dd HH:mm"})
                        private String recruitmentStartDate; // 모집 시작일
                        @Schema(description = "모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
                        private RecruitmentDeadLine recruitmentDeadline; //모집 마감일
                        @Schema(description = "모집 공고 링크")
                        private String recruitmentAnnouncementLink; //모집 공고 링크
                        @Schema(description = "모집 지역",allowableValues = {"SEOUL", "GANGNAM","MAPO","GURO_GARSAN","BUNDANG_PANGYO","ETC","YEOUIDO"})
                        private String recruitmentAddress; //지역
                        @Schema(description = "제목")
                        private String title; //title
                        @Schema(description = "내용")
                        private String content; //내용
                        @Schema(description = "내부 타입",allowableValues = {"HTML"})
                        private String textType;
                    public static class RecruitmentDeadLine {
                        @Schema(description = "채용 공고 마감 종류", allowableValues = {"ALL_TIME", "CLOSE_WHEN_RECRUITMENT", "DUE_DATE","EXPIRED"})
                        private String deadlineType;
                        @Schema(description = "모집 마감일/ deadlineType이 DUE_DATE인 경우에만 입력, 모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm"})
                        private String recruitmentDeadline; //모집 마감일
                    }

                    만들어진 공고를 위 DTO에 맞춰서 json 객체로 만들어서 반환해줘.
                    json에 들어갈 key값들은 위의 DTO에 있는 변수명을 따라가면 되고, 그에 맞는 값을 넣어주면 돼.
                    company는 절대로 AI Career는 들어갈 수 없어 본문 내용에서 회사명을 찾아서 이에 맞게 넣어줘.
                    announcementLink에는 %s을 넣어주고,
                    recruitmentAddress에는 일하는 곳의 주소를 기준으로 넣어줘.
                    String recruitmentStartDate, String recruitmentDeadline 은 "yyyy-MM-dd HH:mm" 형식으로 넣어줘.
                    본문 내용에 채용모집 시작일이 없다면 recruitmentStartDate는 현재시간으로 해줘.
                    본문 내용에 채용모집 마감일이 존재하는 경우에만 deadlineType을 DUE_DATE로 해주고, 만약 마감일이 현재시간보다 이전이면 EXPIRED로 해줘.
                    ALL_TIME, CLOSE_WHEN_RECRUITMENT,EXPIRED 인 경우에는 recruitmentDeadline을 넣어주지 않아도 돼.
                    ALL_TIME은 상시 채용, CLOSE_WHEN_RECRUITMENT은 모집이 끝날 때까지, EXPIRED는 마감된 경우야.
                    deadlineType이 DUE_DATE 마감 기한이 정해져있는 경우야.이 때에만 recruitmentDeadline에 값을 넣어야하는데, 이 때 현재 날짜보다 이전인 경우에는 deadlineType을 EXPIRED로 설정해줘.
                    content에는 공고 내용을 너가 예쁘게 작성해서 넣어줘. 기본적으로 원본 글 내용을 유지하는 방향이었으면 좋겠어.
                    content의 길이는 10줄 이상이면 좋겠고, 볼드체나 글씨 크기 등 스타일 요소도 너가 예쁘게 작성해줘.
                    p 태그나 div 태그 등을 이용해서 내용을 구분해줘.
                    """.formatted(url);

                Map<String, String> userMessage2 = new HashMap<>();
                userMessage2.put("role", "user");
                userMessage2.put("content", userMessage2Content);

                requestBody.put("messages", List.of(userMessage1, userMessage2));

                String requestBodyJson = objectMapper.writeValueAsString(requestBody);

                HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

                ResponseEntity<String> response = restTemplate.exchange(gptEndpoint, HttpMethod.POST, entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    finalResponse.append(response.getBody());
                } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    log.warn("Rate limit reached. Waiting for {} ms", RATE_LIMIT_WAIT_TIME_MS);
                    Thread.sleep(RATE_LIMIT_WAIT_TIME_MS);
                } else {
                    log.error("Error in response from OpenAI: {}", response.getStatusCode());
                }
            }
            return extractContentAndConvert(finalResponse.toString());
        } catch (Exception e) {
            log.error("Failed to request OpenAI: ", e);
        }
        return null;
    }

    // HTML 태그와 자바스크립트 코드를 제거하고 줄바꿈을 정리하는 함수
    private String cleanHtmlTags(String text) {
        // HTML 태그 제거 및 공백 정리
        return text.replaceAll("<[^>]*>", "")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // 요청 메시지를 청크로 분할하는 함수
    private List<String> splitIntoChunks(String text) {
        int maxLength = 25000;
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += maxLength) {
            chunks.add(text.substring(i, Math.min(length, i + maxLength)));
        }
        return chunks;
    }

    // JSON 응답에서 content 부분 추출 및 객체 변환
    public RecruitmentRequirementDto.RecruitmentPost extractContentAndConvert(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            String content = rootNode.path("choices").get(0).path("message").path("content").asText();
            log.info("Extracted content: {}", content);
            return convertToRecruitmentPost(content);
        } catch (Exception e) {
            log.error("Failed to extract and convert content from JSON: ", e);
            return null;
        }
    }

    // JSON 데이터를 RecruitmentPost 객체로 변환
    public RecruitmentRequirementDto.RecruitmentPost convertToRecruitmentPost(String jsonData) {
        try {
            RecruitmentRequirementDto.RecruitmentPost recruitmentPost = objectMapper.readValue(jsonData, RecruitmentRequirementDto.RecruitmentPost.class);
            recruitmentPost.setMainImage(null);
            recruitmentPost.setSubImage(Collections.emptyList());
            log.info("RecruitmentPost JSON: {}", objectMapper.writeValueAsString(recruitmentPost));
            return recruitmentPost;
        } catch (Exception e) {
            log.error("Failed to convert JSON to RecruitmentPost: ", e);
            return null;
        }
    }
}
