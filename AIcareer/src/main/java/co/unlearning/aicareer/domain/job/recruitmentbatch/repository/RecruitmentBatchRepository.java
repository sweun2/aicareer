package co.unlearning.aicareer.domain.job.recruitmentbatch.repository;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitmentbatch.RecruitmentBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentBatchRepository extends JpaRepository<RecruitmentBatch,Integer> {
    Optional<RecruitmentBatch> findRecruitmentBatchByRecruitment(Recruitment recruitment);
}

