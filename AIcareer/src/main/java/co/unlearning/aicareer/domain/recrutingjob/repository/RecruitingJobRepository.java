package co.unlearning.aicareer.domain.recrutingjob.repository;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RecruitingJobRepository extends JpaRepository<RecruitingJob,Integer> {
}
