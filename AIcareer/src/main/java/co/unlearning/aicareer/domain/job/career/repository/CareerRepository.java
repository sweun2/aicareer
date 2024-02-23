package co.unlearning.aicareer.domain.job.career.repository;

import co.unlearning.aicareer.domain.job.career.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career,Integer> {
}
