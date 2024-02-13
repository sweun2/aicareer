package co.unlearning.aicareer.domain.recruitment.repository;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment,Integer>, JpaSpecificationExecutor<Recruitment> {
    Optional<Recruitment> findByUid(String uid);

    Page<Recruitment> findAll(Specification<Recruitment> specification, Pageable pageable);

    @Query("SELECT r FROM Recruitment r JOIN Company c on r.company = c WHERE c.companyName LIKE %:search% OR r.title LIKE %:search% ORDER BY c.companyName, r.title")
    Page<Recruitment> findRecruitmentsByCompanyNameAndTitle(@Param("search") String search, Pageable pageable);
}

