package co.unlearning.aicareer.domain.job.recruitment.service;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentBatch;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentBatchRepository;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.global.email.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecruitmentBatchService {
    private final RecruitmentService recruitmentService;
    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentBatchRepository recruitmentBatchRepository;
    private final EmailService emailService;
    @Async
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void processBadResponseRecruitment(Recruitment recruitment) {
        RestTemplate restTemplate = new RestTemplate();
        String recruitmentLink = recruitment.getRecruitmentAnnouncementLink();

        ResponseEntity<String> response = restTemplate.getForEntity(
                recruitmentLink, String.class);
        List<Integer> statusCodes = List.of(400,401,402,403,404);
        if(statusCodes.contains(response.getStatusCode().value())) {
            Optional<RecruitmentBatch> recruitmentBatchOptional = recruitmentBatchRepository.findRecruitmentBatchByRecruitment(recruitment);
            if (recruitmentBatchOptional.isEmpty()) {
                recruitmentBatchRepository.save(RecruitmentBatch.builder()
                                .recruitment(recruitment)
                                .badResponseCnt(1)
                        .build());
            } else {
                Integer badResponseCnt = recruitmentBatchOptional.get().getBadResponseCnt();
                if(badResponseCnt >= 3) {
                    recruitment.setRecruitmentDeadline(LocalDateTime.of(2000,1,1,0,0));
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

    @Scheduled(cron = "0 0 11 ? * SAT", zone="Asia/Seoul")
    public void sendWeeklyTopHitsRecruitmentMail() {
        emailService.sendRecruitMailEveryWeek();
    }
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireRecruitments() {
        List<Recruitment> dueDateRecruitments = recruitmentService.findAllRecruitmentsWithDeadLineType(RecruitmentDeadlineType.DUE_DATE);
        dueDateRecruitments.forEach(recruitment -> {
            if(recruitment.getRecruitmentDeadline().isBefore(LocalDateTime.now())) {
                recruitment.setRecruitmentDeadline(LocalDateTime.of(2000,1,1,0,0));
                recruitment.setRecruitmentDeadlineType(RecruitmentDeadlineType.EXPIRED);
            }
        });
        recruitmentRepository.saveAll(dueDateRecruitments);

        List<Recruitment> recruitmentList = recruitmentService.findAllNotInRecruitmentDeadlineTypes(List.of(RecruitmentDeadlineType.DUE_DATE,RecruitmentDeadlineType.EXPIRED));
        new HashSet<>(recruitmentList).stream().toList().forEach(this::processBadResponseRecruitment);
    }
}
