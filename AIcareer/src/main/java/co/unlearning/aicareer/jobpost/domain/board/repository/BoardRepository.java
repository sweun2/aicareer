package co.unlearning.aicareer.jobpost.domain.board.repository;

import co.unlearning.aicareer.jobpost.domain.board.Board;
import co.unlearning.aicareer.jobpost.domain.bookmark.Bookmark;
import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
import co.unlearning.aicareer.jobpost.domain.user.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board,Integer> {
    List<Board> findAllByIsViewIsTrue();
    Optional<Board> findByUid(String uid);
}
