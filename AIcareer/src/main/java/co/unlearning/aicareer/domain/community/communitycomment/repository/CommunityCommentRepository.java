package co.unlearning.aicareer.domain.community.communitycomment.repository;

import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment,Integer> {
    Page<CommunityComment> findAllByCommunityPostingAndIsViewTrueOrderByUploadDateDesc(CommunityPosting communityPosting, Pageable pageable);
    Page<CommunityComment> findAllByCommunityPostingOrderByUploadDateDesc(CommunityPosting communityPosting, Pageable pageable);
    Optional<CommunityComment> findByUid(String uid);
}
