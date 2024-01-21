package co.unlearning.aicareer.domain.education.repository;

import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education,Integer> {
}
