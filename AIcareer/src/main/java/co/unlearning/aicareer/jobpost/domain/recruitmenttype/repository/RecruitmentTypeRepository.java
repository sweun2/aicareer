package co.unlearning.aicareer.jobpost.domain.recruitmenttype.repository;

import co.unlearning.aicareer.jobpost.domain.recruitmenttype.RecruitmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentTypeRepository extends JpaRepository<RecruitmentType,Integer> {
}
