package co.unlearning.aicareer.domain.job.recruitment.repository;

import co.unlearning.aicareer.domain.job.career.Career;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment,Integer>, JpaSpecificationExecutor<Recruitment> {
    Optional<Recruitment> findByUid(String uid);

    Page<Recruitment> findAll(Specification<Recruitment> specification, Pageable pageable);

    @Query("SELECT r FROM Recruitment r JOIN Company c on r.company = c WHERE c.companyName LIKE %:search% OR r.title LIKE %:search% ORDER BY c.companyName, r.title")
    Page<Recruitment> findRecruitmentsByCompanyNameAndTitle(@Param("search") String search, Pageable pageable);

    List<Recruitment> findAllByRecruitmentDeadlineTypeIsNot(RecruitmentDeadlineType recruitmentDeadlineType);

    List<Recruitment> findAllByRecruitmentDeadlineTypeAndRecruitmentDeadlineIsBefore(RecruitmentDeadlineType recruitmentDeadlineType, LocalDateTime deadline);
    @Query("SELECT r FROM Recruitment r JOIN Career c on c.recruitment = r WHERE c.annualLeave = :annualLeave AND r.uploadDate >= :startDate AND r.uploadDate <= :endDate")
    List<Recruitment> findAllRecruitmentsDateRange(@Param("annualLeave")Career.AnnualLeave annualLeave,@Param("startDate") LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

    List<Recruitment> findAllByRecruitmentDeadlineType(RecruitmentDeadlineType recruitmentDeadlineType);

}

