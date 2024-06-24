package co.unlearning.aicareer.domain.community.communityvote.repository;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import co.unlearning.aicareer.domain.community.communityvote.VoteOption;
import co.unlearning.aicareer.domain.community.communityvote.VoteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteUserRepository extends JpaRepository<VoteUser,Integer> {

    Optional<VoteUser> findByUserAndCommunityVoteAndVoteOption(User user, CommunityVote communityVote, VoteOption voteOption);

    List<VoteUser> findByUserAndCommunityVote(User user, CommunityVote communityVote);
}
