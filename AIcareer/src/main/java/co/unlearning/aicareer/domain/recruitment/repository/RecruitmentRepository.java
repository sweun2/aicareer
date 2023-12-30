package co.unlearning.aicareer.domain.recruitment.repository;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment,Integer>, JpaSpecificationExecutor<Recruitment> {
    Optional<Recruitment> findByUid(Long uid);
}
