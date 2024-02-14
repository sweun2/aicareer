package co.unlearning.aicareer.domain.Image.repository;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {
    Optional<Image> findByImageUrl(String imageUrl);
    List<Image> findAllByRecruitmentIsNullAndBoardIsNull();
}
