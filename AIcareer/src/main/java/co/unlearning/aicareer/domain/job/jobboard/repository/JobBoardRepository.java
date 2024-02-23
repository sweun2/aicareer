package co.unlearning.aicareer.domain.job.jobboard.repository;

import co.unlearning.aicareer.domain.job.jobboard.JobBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobBoardRepository extends JpaRepository<JobBoard,Integer> {
    List<JobBoard> findAllByIsViewIsTrue();
    Optional<JobBoard> findByUid(String uid);
}
