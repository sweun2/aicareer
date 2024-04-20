package co.unlearning.aicareer.domain.community.communitypostinguser.repository;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityPostingUserRepository extends JpaRepository<CommunityPostingUser,Integer> {

    Optional<CommunityPostingUser> findCommunityPostingUserByCommunityPostingAndUser(CommunityPosting communityPosting, User user);
}
