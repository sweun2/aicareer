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

    @Query("SELECT c FROM CommunityPostingUser c WHERE c.id IN (" +
            "  SELECT MIN(c2.id) FROM CommunityPostingUser c2 " +
            "  WHERE (c2.communityPosting.id, c2.user.id) IN (" +
            "    SELECT c3.communityPosting.id, c3.user.id " +
            "    FROM CommunityPostingUser c3 " +
            "    GROUP BY c3.communityPosting.id, c3.user.id " +
            "    HAVING COUNT(c3.id) > 1" +
            "  ) " +
            "  GROUP BY c2.communityPosting.id, c2.user.id" +
            ")")
    List<CommunityPostingUser> findDuplicateCommunityPostingUsers();
}
