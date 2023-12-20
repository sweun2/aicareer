package co.unlearning.aicareer.domain.recruitment.service;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    public Recruitment findRecruitmentInfo(String uid) {
        return recruitmentRepository.findByUid(Long.valueOf(uid)).orElseThrow(
                () -> new ResponseStatusException(HttpStatusCode.valueOf(404),"text")
        );
    }
    public Recruitment findAllRecruitmentInfo() {
        return recruitmentRepository.findByUid().orElseThrow(
                () -> new ResponseStatusException(HttpStatusCode.valueOf(404),"text")
        );
    }
}
