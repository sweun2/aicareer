package co.unlearning.aicareer.domain.community.communityvote.repository;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityVoteRepository extends JpaRepository<CommunityVote,Integer> {
}
