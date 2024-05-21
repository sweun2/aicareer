package co.unlearning.aicareer.domain.job.recruitmentbatch.service;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitmentbatch.repository.RecruitmentBatchRepository;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.RecruitmentBatch;
import co.unlearning.aicareer.global.email.service.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentBatchService {
    private final RecruitmentService recruitmentService;
    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentBatchRepository recruitmentBatchRepository;
    private final EmailService emailService;

    private final ServletContext servletContext;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${clova.ocr.api-url}")
    private String ocrApiUrl;
    @Value("${clova.ocr.secret}")
    private String ocrSecret;

    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void processBadResponseRecruitment(Recruitment recruitment) {
        RestTemplate restTemplate = new RestTemplate();
        String recruitmentLink = recruitment.getRecruitmentAnnouncementLink();

        ResponseEntity<String> response = restTemplate.getForEntity(
                recruitmentLink, String.class);
        List<Integer> statusCodes = List.of(400, 401, 402, 403, 404);
        if (statusCodes.contains(response.getStatusCode().value())) {
            Optional<RecruitmentBatch> recruitmentBatchOptional = recruitmentBatchRepository.findRecruitmentBatchByRecruitment(recruitment);
            if (recruitmentBatchOptional.isEmpty()) {
                recruitmentBatchRepository.save(RecruitmentBatch.builder()
                        .recruitment(recruitment)
                        .badResponseCnt(1)
                        .build());
            } else {
                Integer badResponseCnt = recruitmentBatchOptional.get().getBadResponseCnt();
                if (badResponseCnt >= 3) {
                    recruitment.setRecruitmentDeadline(LocalDateTime.of(2000, 1, 1, 0, 0));
                    recruitment.setRecruitmentDeadlineType(RecruitmentDeadlineType.EXPIRED);
                    recruitmentRepository.save(recruitment);
                }
            }
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

    /*@Transactional
    public Recruitment postRecruitmentFromOCRText() {

    }*/
    public String performOcr(MultipartFile image) {
        log.info("perform OCR");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-OCR-SECRET", ocrSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(ocrApiUrl, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }
}
