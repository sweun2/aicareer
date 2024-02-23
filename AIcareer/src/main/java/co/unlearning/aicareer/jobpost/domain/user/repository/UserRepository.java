package co.unlearning.aicareer.jobpost.domain.user.repository;

import co.unlearning.aicareer.jobpost.domain.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByRecommender(String recommender);
    Optional<User> findByEmail(String email);
    List<User> findAll();
}
