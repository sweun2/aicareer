package co.unlearning.aicareer.domain.job.recruitmentImage.repository;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitmentImage.RecruitmentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentImageRepository extends JpaRepository<RecruitmentImage,Integer> {
    void deleteBoardImageByRecruitment(Recruitment recruitment);
    List<RecruitmentImage> findAllByRecruitment(Recruitment recruitment);
    Optional<RecruitmentImage> findByRecruitment(Recruitment recruitment);
    Optional<RecruitmentImage> findByImage(Image image);
    Optional<RecruitmentImage> findFirstByRecruitmentOrderByImageOrderDesc(Recruitment recruitment);


}
