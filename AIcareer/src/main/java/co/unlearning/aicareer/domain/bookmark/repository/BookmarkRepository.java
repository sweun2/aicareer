package co.unlearning.aicareer.domain.bookmark.repository;

import co.unlearning.aicareer.domain.bookmark.Bookmark;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Integer> {
    Optional<Bookmark> findByUserAndRecruitment(User user, Recruitment recruitment);
    List<Bookmark> findAllByUser(User user);
}
