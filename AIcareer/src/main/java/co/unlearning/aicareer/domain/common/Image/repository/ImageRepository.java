package co.unlearning.aicareer.domain.common.Image.repository;

import co.unlearning.aicareer.domain.common.Image.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {
    Optional<Image> findByImageUrl(String imageUrl);
    List<Image> findAllByIsRelatedFalse();
    void deleteByImageUrl(String url);
}
