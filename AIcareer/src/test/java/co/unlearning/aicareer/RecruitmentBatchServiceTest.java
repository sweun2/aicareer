package co.unlearning.aicareer;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.RecruitmentBatch;
import co.unlearning.aicareer.domain.job.recruitmentbatch.repository.RecruitmentBatchRepository;
import co.unlearning.aicareer.domain.job.recruitmentbatch.service.RecruitmentBatchService;
import co.unlearning.aicareer.global.email.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RecruitmentBatchServiceTest {

    @Mock
    private RecruitmentService recruitmentService;

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private RecruitmentBatchRepository recruitmentBatchRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RecruitmentBatchService recruitmentBatchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessBadResponseRecruitment() {
        System.out.println( "testProcessBadResponseRecruitment" );
        Recruitment recruitment = Recruitment.builder()
                .recruitmentAnnouncementLink("https://career.carrotins.com/o/100842")
                .build();

        RestTemplate restTemplate = new RestTemplate();
        String recruitmentLink = recruitment.getRecruitmentAnnouncementLink();

        try {
            System.out.println("test: " + recruitmentLink);
            ResponseEntity<String> response = restTemplate.getForEntity(recruitmentLink, String.class);
            System.out.println(response.getStatusCode());
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
}