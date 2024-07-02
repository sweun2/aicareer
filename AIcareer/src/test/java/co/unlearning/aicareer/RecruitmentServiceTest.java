package co.unlearning.aicareer;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.RecruitmentBatch;
import co.unlearning.aicareer.domain.job.recruitmentbatch.repository.RecruitmentBatchRepository;
import co.unlearning.aicareer.domain.job.recruitmentbatch.service.RecruitmentBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecruitmentServiceTest {

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private RecruitmentBatchRepository recruitmentBatchRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecruitmentBatchService recruitmentBatchService;

    private Recruitment recruitment;
    private RecruitmentBatch recruitmentBatch;

    @BeforeEach
    void setUp() {
        recruitment = new Recruitment();
        recruitment.setRecruitmentAnnouncementLink("https://www.aicareer.co.kr/.com");
    }

    @Test
    void testProcessBadResponseRecruitment_WhenBadResponseAndFirstTime() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST));
        when(recruitmentBatchRepository.findRecruitmentBatchByRecruitment(any(Recruitment.class)))
                .thenReturn(Optional.empty());

        recruitmentBatchService.processBadResponseRecruitment(recruitment);

        verify(recruitmentBatchRepository, times(1)).save(any(RecruitmentBatch.class));
    }

    @Test
    void testProcessBadResponseRecruitment_WhenBadResponseAndBadResponseCntLessThan3() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST));
        RecruitmentBatch recruitmentBatch = RecruitmentBatch.builder()
                .recruitment(recruitment)
                .badResponseCnt(2)
                .build();
        when(recruitmentBatchRepository.findRecruitmentBatchByRecruitment(any(Recruitment.class)))
                .thenReturn(Optional.of(recruitmentBatch));

        recruitmentBatchService.processBadResponseRecruitment(recruitment);

        verify(recruitmentRepository, never()).save(recruitment);
    }

    @Test
    void testProcessBadResponseRecruitment_WhenBadResponseAndBadResponseCntGreaterThanEqual3() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST));
        RecruitmentBatch recruitmentBatch = RecruitmentBatch.builder()
                .recruitment(recruitment)
                .badResponseCnt(3)
                .build();
        when(recruitmentBatchRepository.findRecruitmentBatchByRecruitment(any(Recruitment.class)))
                .thenReturn(Optional.of(recruitmentBatch));

        recruitmentBatchService.processBadResponseRecruitment(recruitment);

        assertEquals(LocalDateTime.of(2000, 1, 1, 0, 0), recruitment.getRecruitmentDeadline());
        assertEquals(RecruitmentDeadlineType.EXPIRED, recruitment.getRecruitmentDeadlineType());
        verify(recruitmentRepository, times(1)).save(recruitment);
    }
}