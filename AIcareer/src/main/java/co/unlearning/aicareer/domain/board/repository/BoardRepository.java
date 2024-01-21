package co.unlearning.aicareer.domain.board.repository;

import co.unlearning.aicareer.domain.board.Board;
import co.unlearning.aicareer.domain.bookmark.Bookmark;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.user.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Integer> {
    List<Board> findAllByIsViewIsTrue();
    Optional<Board> findByUid(String uid);
}
