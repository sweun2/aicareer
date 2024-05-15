package co.unlearning.aicareer.domain.job.boardimage.repository;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage,Integer> {
    void deleteBoardImageByBoard(Board board);
    List<BoardImage> findAllByBoard(Board board);
    Optional<BoardImage> findByImage(Image image);
}
