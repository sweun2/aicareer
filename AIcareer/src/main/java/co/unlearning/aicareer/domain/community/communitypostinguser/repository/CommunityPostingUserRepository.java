package co.unlearning.aicareer.domain.community.communitypostinguser.repository;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostingUserRepository extends JpaRepository<CommunityPostingUser,Integer> {

    Optional<CommunityPostingUser> findCommunityPostingUserByCommunityPostingAndUser(CommunityPosting communityPosting, User user);
    @Query("SELECT cpu FROM CommunityPostingUser cpu " +
            "JOIN cpu.communityPosting cp " +
            "WHERE cpu.isReport = false " +
            "AND cpu.isRecommend = false " +
            "AND cp.writer.id <> cpu.user.id")
    List<CommunityPostingUser> findCommunityPostingUserAllOptionFalseAndNotWriter();
}
