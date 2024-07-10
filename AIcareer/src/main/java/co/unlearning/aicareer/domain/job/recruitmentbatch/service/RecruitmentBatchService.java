package co.unlearning.aicareer.domain.job.recruitmentbatch.service;

import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitmentbatch.repository.RecruitmentBatchRepository;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.RecruitmentBatch;
import co.unlearning.aicareer.global.email.service.EmailService;
import co.unlearning.aicareer.global.utils.ImageUtil;
import co.unlearning.aicareer.global.utils.MultipartFileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentBatchService {
    private final RecruitmentService recruitmentService;
    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentBatchRepository recruitmentBatchRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final EmailService emailService;

    private final ServletContext servletContext;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${clova.ocr.api-url}")
    private String ocrApiUrl;
    @Value("${clova.ocr.secret}")
    private String ocrSecret;
    public boolean isValidImageFormat(String extension) {
        return extension.matches("(?i)^(jpg|jpeg|png|tif|tiff|pdf)$");
    }

    public String getFileExtension(String url) {
        try {
            return url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }
    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void processBadResponseRecruitment(Recruitment recruitment) {
        RestTemplate restTemplate = new RestTemplate();
        String recruitmentLink = recruitment.getRecruitmentAnnouncementLink();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(recruitmentLink, String.class);
        } catch (HttpClientErrorException.BadRequest |
                 HttpClientErrorException.Unauthorized |
                 HttpClientErrorException.Forbidden |
                 HttpClientErrorException.NotFound |
                 HttpClientErrorException.MethodNotAllowed |
                 HttpClientErrorException.Conflict e) {

            Optional<RecruitmentBatch> recruitmentBatchOptional = recruitmentBatchRepository.findRecruitmentBatchByRecruitment(recruitment);
            if (recruitmentBatchOptional.isEmpty()) {
                System.out.println("Bad Response: " + recruitment.getRecruitmentAnnouncementLink());
                recruitmentBatchRepository.save(RecruitmentBatch.builder()
                        .recruitment(recruitment)
                        .badResponseCnt(1)
                        .build());
            } else {
                System.out.println("Bad Response: " + recruitment.getRecruitmentAnnouncementLink());
                Integer badResponseCnt = recruitmentBatchOptional.get().getBadResponseCnt();
                if (badResponseCnt >= 3) {
                    recruitment.setRecruitmentDeadline(LocalDateTime.of(2000, 1, 1, 0, 0));
                    recruitment.setRecruitmentDeadlineType(RecruitmentDeadlineType.EXPIRED);
                    recruitmentRepository.save(recruitment);
                }
            }
        } catch (Exception e) {
            System.err.println("예외 발생: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendMailEveryDayWithInterest() {
        emailService.sendRecruitMailEveryDay();
    }

    @Scheduled(cron = "0 0 11 ? * SAT", zone = "Asia/Seoul")
    public void sendWeeklyTopHitsRecruitmentMail() {
        emailService.sendRecruitMailEveryWeek();
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireRecruitments() {
        List<Recruitment> dueDateRecruitments = recruitmentService.findAllRecruitmentsWithDeadLineType(RecruitmentDeadlineType.DUE_DATE);
        dueDateRecruitments.forEach(recruitment -> {
            if (recruitment.getRecruitmentDeadline().isBefore(LocalDateTime.now())) {
                recruitment.setRecruitmentDeadline(LocalDateTime.of(2000, 1, 1, 0, 0));
                recruitment.setRecruitmentDeadlineType(RecruitmentDeadlineType.EXPIRED);
            }
        });
        recruitmentRepository.saveAll(dueDateRecruitments);

        List<Recruitment> recruitmentList = recruitmentService.findAllNotInRecruitmentDeadlineTypes(List.of(RecruitmentDeadlineType.DUE_DATE, RecruitmentDeadlineType.EXPIRED));
        new HashSet<>(recruitmentList).stream().toList().forEach(this::processBadResponseRecruitment);
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void removeUnrelatedImage() {
        imageRepository.deleteAll(imageRepository.findAllByIsRelatedFalse());
    }

    /*@Transactional
    public Recruitment postRecruitmentFromOCRText() {

    }*/
    public String performOcr(MultipartFile file, String imageUrl) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-OCR-SECRET", ocrSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> clovaExtension = List.of("jpg", "jpeg", "png", "pdf", "tiff");
        String extension = ImageUtil.getExtension(imageUrl);

        // SVG 및 WebP 변환 처리
        if (extension.equalsIgnoreCase("svg") || extension.equalsIgnoreCase("webp")) {
            file = MultipartFileUtil.convertImageToSupportedFormat(file);
            extension = "jpg"; // 변환 후 확장자를 JPG로 설정
        }
        // 이미지 파일을 Base64로 인코딩
        String base64EncodedImage = MultipartFileUtil.convertToBase64(file);

        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("format", extension);
        imageMap.put("data", base64EncodedImage);
        imageMap.put("name", imageUrl);

        Map<String, Object> body = new HashMap<>();
        body.put("version", "V2");
        body.put("requestId", UUID.randomUUID().toString());
        body.put("timestamp", System.currentTimeMillis());
        body.put("images", List.of(imageMap));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(ocrApiUrl, HttpMethod.POST, requestEntity, String.class);


        return response.getBody();
    }
    private String extractInferText(String jsonResponse) throws Exception {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode images = root.path("images");
            if (images.isArray()) {
                for (JsonNode image : images) {
                    if (image.path("message").asText().equals("SUCCESS")) {
                        JsonNode fields = image.path("fields");
                        if (fields.isArray()) {
                            for (JsonNode field : fields) {
                                return field.path("inferText").asText();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
        return null;
    }
}
