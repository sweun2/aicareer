package co.unlearning.aicareer.domain.common.sitemap.repository;

import co.unlearning.aicareer.domain.common.sitemap.SiteMap;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface SiteMapRepository extends JpaRepository<SiteMap,Integer> {
    @Query("SELECT s FROM SiteMap s WHERE s.lastModified >= :oneYearAgo")
    List<SiteMap> findSiteMapsLastModifiedWithinOneYear(@Param("oneYearAgo") LocalDateTime oneYearAgo);
    @NonNull
    List<SiteMap> findAll();
    Optional<SiteMap> findByUid(String uid);
}
