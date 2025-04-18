package co.unlearning.aicareer.domain.job.recruitmentbatch.controller;

import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.job.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.GptService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.service.RecruitmentBatchService;
import co.unlearning.aicareer.global.utils.MultipartFileUtil;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.*;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@Tag(name = "recruitment batch", description = "스케줄링 api")
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/batch")
public class RecruitmentBatchController {
    private final RecruitmentService recruitmentService;
    private final RecruitmentBatchService recruitmentBatchService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final GptService gptService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "자동 글 올리기", description = "url 넣으면 자동화!?!?")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.DATE_BAD_REQUEST),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_ENUM_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED)

    })
    @GetMapping("/extract")
    @PostMapping("/extractTextFromUrl")
    public ResponseEntity<RecruitmentResponseDto.RecruitmentInfo> extractTextFromUrl(@RequestParam("url") String url) {
        WebDriver driver = null;
        try {
            // WebDriver 경로 설정 (크롬 드라이버 예제)
            System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

            // WebDriver 인스턴스 생성
            driver = new ChromeDriver();

            // URL 로드
            driver.get(url);

            // 페이지의 HTML 가져오기
            String pageSource = driver.getPageSource();

            // JSoup을 사용하여 HTML 파싱
            Document doc = Jsoup.parse(pageSource);
            Elements scripts = doc.select("script");
            for (Element script : scripts) {
                script.remove();
            }

            Elements styles = doc.select("style");
            for (Element style : styles) {
                style.remove();
            }

            Elements images = doc.select("img");
            StringBuilder result = new StringBuilder();
            for (Element img : images) {
                String imgUrl = img.absUrl("src");
                if (!imgUrl.isEmpty()) {
                    String fileExtension = recruitmentBatchService.getFileExtension(imgUrl);
                    if (recruitmentBatchService.isValidImageFormat(fileExtension)) {
                        MultipartFile file = MultipartFileUtil.convertUrlToMultipartFile(imgUrl);
                        String ocrResult = recruitmentBatchService.performOcr(file, imgUrl);
                        JsonNode ocrResultJson = objectMapper.readTree(ocrResult);
                        JsonNode imagesNode = ocrResultJson.path("images");

                        for (JsonNode imageNode : imagesNode) {
                            JsonNode fieldsNode = imageNode.path("fields");
                            for (JsonNode fieldNode : fieldsNode) {
                                String inferText = fieldNode.path("inferText").asText();
                                result.append(inferText).append(" ");
                            }
                        }
                    } else {
                        log.info("Unsupported image format: " + fileExtension);
                    }
                }
            }

            String pageText = doc.body().html();
            String title = doc.html();
            result.append(pageText.replaceAll("<[^>]*>", ""));
            return ResponseEntity.ok(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.addRecruitmentPost(gptService.requestToOpenAI(title, result.toString(), url))));
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(500).build();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @GetMapping("/clean-unrelated-image")
    public ResponseEntity<Void> cleanText() {
        recruitmentBatchService.removeUnrelatedImage();
        return ResponseEntity.ok().build();
    }
}