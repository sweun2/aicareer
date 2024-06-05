package co.unlearning.aicareer.domain.community.communitycommentuser.repository;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityCommentUserRepository extends JpaRepository<CommunityCommentUser,Integer> {
    Optional<CommunityCommentUser> findByUserAndCommunityComment(User user, CommunityComment communityComment);
    Optional<CommunityCommentUser> findByCommunityComment(CommunityComment communityComment);

    @Query("SELECT ccu FROM CommunityCommentUser ccu " +
            "JOIN ccu.communityComment cc " +
            "WHERE ccu.isReport = false " +
            "AND ccu.isRecommend = false " +
            "AND cc.id <> ccu.user.id")
    List<CommunityCommentUser> findCommunityCommentUserAllOptionFalseAndNotWriter();
}
