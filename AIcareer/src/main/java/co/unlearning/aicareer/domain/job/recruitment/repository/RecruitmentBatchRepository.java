package co.unlearning.aicareer.domain.job.recruitment.repository;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentBatch;
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
public interface RecruitmentBatchRepository extends JpaRepository<RecruitmentBatch,Integer> {
}

