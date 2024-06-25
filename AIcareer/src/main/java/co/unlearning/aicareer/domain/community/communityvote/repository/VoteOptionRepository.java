package co.unlearning.aicareer.domain.community.communityvote.repository;

import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import co.unlearning.aicareer.domain.community.communityvote.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption,Integer> {
    Optional<VoteOption> findByOptionAndCommunityVote(String option, CommunityVote communityVote);
}
