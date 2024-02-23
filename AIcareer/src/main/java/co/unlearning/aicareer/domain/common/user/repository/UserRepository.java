package co.unlearning.aicareer.domain.common.user.repository;

import co.unlearning.aicareer.domain.common.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByRecommender(String recommender);
    Optional<User> findByEmail(String email);
    List<User> findAll();
}
