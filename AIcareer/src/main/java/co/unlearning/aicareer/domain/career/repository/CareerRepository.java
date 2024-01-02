package co.unlearning.aicareer.domain.career.repository;

import co.unlearning.aicareer.domain.career.Career;
import co.unlearning.aicareer.domain.career.dto.CareerResponseDto;
import co.unlearning.aicareer.domain.education.Education;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career,Integer> {
}
