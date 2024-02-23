package co.unlearning.aicareer.domain.blog.board.repository;

import co.unlearning.aicareer.domain.blog.board.BlogBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogBoardRepository extends JpaRepository<BlogBoard,Integer> {
    List<BlogBoard> findAllByIsViewIsTrue();
    Optional<BlogBoard> findByUid(String uid);
}
