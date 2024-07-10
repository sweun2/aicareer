package co.unlearning.aicareer.domain.community.communitycomment.repository;

import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CommunityComment c WHERE c.uid = :parentUid")
    Optional<CommunityComment> lockParentComment(@Param("parentUid") String parentUid);
}
