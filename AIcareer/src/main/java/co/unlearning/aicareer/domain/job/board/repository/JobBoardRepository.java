package co.unlearning.aicareer.domain.job.board.repository;

import co.unlearning.aicareer.domain.job.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobBoardRepository extends JpaRepository<Board,Integer> {
    List<Board> findAllByIsViewIsTrue();
    Optional<Board> findByUid(String uid);
}
