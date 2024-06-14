package co.unlearning.aicareer.domain.community.communitypostingimage.repository;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostingImageRepository extends JpaRepository<CommunityPostingImage,Integer> {
    void deleteBoardImageByCommunityPosting(CommunityPosting communityPosting);
    List<CommunityPostingImage> findAllByCommunityPosting(CommunityPosting communityPosting);
    Optional<CommunityPostingImage> findByCommunityPosting(CommunityPosting communityPosting);
    Optional<CommunityPostingImage> findByImage(Image image);
    Optional<CommunityPostingImage> findFirstByCommunityPostingOrderByImageOrderDesc(CommunityPosting communityPosting);
}
