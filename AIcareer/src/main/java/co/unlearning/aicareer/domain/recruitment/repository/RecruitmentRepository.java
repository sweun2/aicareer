package co.unlearning.aicareer.domain.recruitment.repository;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment,Integer> {
    Optional<Recruitment> findByUid(Long uid);
}
