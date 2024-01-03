package co.unlearning.aicareer.domain.recruitment.repository;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment,Integer>, JpaSpecificationExecutor<Recruitment> {
    Optional<Recruitment> findByUid(String uid);
    Page<Recruitment> findAll(Specification<Recruitment> specification, Pageable pageable);
}
