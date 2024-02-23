package co.unlearning.aicareer.jobpost.domain.recrutingjob.repository;

import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
import co.unlearning.aicareer.jobpost.domain.recrutingjob.RecruitingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RecruitingJobRepository extends JpaRepository<RecruitingJob,Integer> {
}
