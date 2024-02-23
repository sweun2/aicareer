package co.unlearning.aicareer.domain.job.recruitmenttype.repository;

import co.unlearning.aicareer.domain.job.recruitmenttype.RecruitmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentTypeRepository extends JpaRepository<RecruitmentType,Integer> {
}
