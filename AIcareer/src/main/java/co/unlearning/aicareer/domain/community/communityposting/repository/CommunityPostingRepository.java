package co.unlearning.aicareer.domain.community.communityposting.repository;

import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface CommunityPostingRepository extends JpaRepository<CommunityPosting,Integer> {
    Optional<CommunityPosting> findByUid(String uid);
    Page<CommunityPosting> findAllByIsViewTrueOrderByUploadDateDesc(Pageable pageable);
    Page<CommunityPosting> findAllByContentContainsOrTitleContains(String title,String content, Pageable pageable);

    @Query("SELECT p FROM CommunityPosting p WHERE p.uploadDate >= :startOfDay AND p.uploadDate < :endOfDay ORDER BY (p.hits + p.recommendCnt + p.commentCnt) DESC")
    List<CommunityPosting> findTopPosts(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
}
