package co.unlearning.aicareer.domain.job.education.repository;

import co.unlearning.aicareer.domain.job.education.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education,Integer> {
}
