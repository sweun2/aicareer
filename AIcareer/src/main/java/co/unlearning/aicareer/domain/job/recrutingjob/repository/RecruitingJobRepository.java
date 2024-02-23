package co.unlearning.aicareer.domain.job.recrutingjob.repository;

import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitingJobRepository extends JpaRepository<RecruitingJob,Integer> {
}
