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
    Page<CommunityComment> findAllByCommunityPostingOrderByUploadDateAsc(CommunityPosting communityPosting, Pageable pageable);
    Page<CommunityComment> findAllByCommunityPostingAndIsViewTrueOrderByUploadDateAsc(CommunityPosting communityPosting, Pageable pageable);
    Page<CommunityComment> findAllByCommunityPostingAndParentCommentIsNullOrderByUploadDateAsc(CommunityPosting communityPosting, Pageable pageable);
    Page<CommunityComment> findAllByCommunityPostingAndIsViewTrueAndParentCommentIsNullOrderByUploadDateAsc(CommunityPosting communityPosting, Pageable pageable);
    Page<CommunityComment>  findAllByParentCommentOrderByUploadDateAsc(CommunityComment parentComment, Pageable pageable);
    Page<CommunityComment> findAllByParentCommentAndIsViewTrueOrderByUploadDateAsc(CommunityComment parentComment, Pageable pageable);
    Optional<CommunityComment> findByUid(String uid);
}
