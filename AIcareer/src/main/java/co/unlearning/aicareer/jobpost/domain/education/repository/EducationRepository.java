package co.unlearning.aicareer.jobpost.domain.education.repository;

import co.unlearning.aicareer.jobpost.domain.education.Education;
import co.unlearning.aicareer.jobpost.domain.recruitmenttype.RecruitmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education,Integer> {
}
