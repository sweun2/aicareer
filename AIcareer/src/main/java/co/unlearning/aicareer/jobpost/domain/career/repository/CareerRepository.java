package co.unlearning.aicareer.jobpost.domain.career.repository;

import co.unlearning.aicareer.jobpost.domain.career.Career;
import co.unlearning.aicareer.jobpost.domain.career.dto.CareerResponseDto;
import co.unlearning.aicareer.jobpost.domain.education.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career,Integer> {
}
