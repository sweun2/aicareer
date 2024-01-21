package co.unlearning.aicareer.domain.bookmark.repository;

import co.unlearning.aicareer.domain.bookmark.Bookmark;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Integer> {
    Optional<Bookmark> findByUserAndRecruitment(User user, Recruitment recruitment);
    List<Bookmark> findAllByUser(User user);
}
