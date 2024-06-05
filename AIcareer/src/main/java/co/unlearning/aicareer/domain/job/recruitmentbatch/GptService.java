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
                         "content": "최대한 원본의 text를 살려서 채용공고를 마크다운 형식으로 작성해줘, 회사명은 한개만 존재해야되. 정리되지 않은 채용공고는 다음과 같아.
                         회사는 하나만 적어줘.
                          %s"
                     },
                     {
                         "role": "user",
                         "content": "public static class RecruitmentPost {
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
                         @Schema(description = "모집 직무",allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC","PM_PO","CONSULTANT","SOFTWARE_ENGINEER","SALES","OPERATION"})
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
                         @Schema(description = "모집 시작일, 일자/시간 사이 빈칸 필요", defaultValue = "작성 시 시간", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
                         private String recruitmentStartDate; // 모집 시작일
                         @Schema(description = "모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
                         private RecruitmentDeadLine recruitmentDeadline; //모집 마감일
                         @Schema(description = "모집 공고 링크")
                         private String recruitmentAnnouncementLink; //모집 공고 링크
                         @Schema(description = "모집 지역",allowableValues = {"SEOUL", "GANGNAM","MAPO","GURO_GARSAN_GAME","BUNDANG_PANGYO","ETC"})
                         private String recruitmentAddress; //지역
                         @Schema(description = "제목")
                         private String title; //title
                         @Schema(description = "내용")
                         private String content; //내용
                         @Schema(description = "내부 타입",allowableValues = {"MARKDOWN,HTML"})
                         private String textType;
                        }
                        만들어진 공고를 위 DTO에 맞춰서 json 객체로 만들어서 반환해줘.
                        content에는 공고 내용이 들어가면 되.
                        "
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
