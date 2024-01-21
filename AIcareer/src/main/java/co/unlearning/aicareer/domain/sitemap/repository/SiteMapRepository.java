package co.unlearning.aicareer.domain.sitemap.repository;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.sitemap.SiteMap;
import lombok.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SiteMapRepository extends JpaRepository<SiteMap,Integer> {
    @Query("SELECT s FROM SiteMap s WHERE s.lastModified >= :oneYearAgo")
    List<SiteMap> findSiteMapsLastModifiedWithinOneYear(@Param("oneYearAgo") LocalDateTime oneYearAgo);
    @NonNull
    List<SiteMap> findAll();
    Optional<SiteMap> findByUid(String uid);
}
