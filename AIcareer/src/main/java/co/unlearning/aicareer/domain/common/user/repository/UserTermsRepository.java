package co.unlearning.aicareer.domain.common.user.repository;

import co.unlearning.aicareer.domain.common.user.UserTerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTermsRepository extends JpaRepository<UserTerms, Integer> {
}

