package co.unlearning.aicareer.domain.job.recruitmentImage.service;

import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
import co.unlearning.aicareer.domain.job.recruitmentImage.RecruitmentImage;
import co.unlearning.aicareer.domain.job.recruitmentImage.repository.RecruitmentImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RecruitmentImageService {
    private final ImageService imageService;
    private final EntityManager entityManager;
    private final RecruitmentImageRepository recruitmentImageRepository;
    public void removeRecruitmentImage(RecruitmentImage recruitmentImage) {
        String imageUrl = recruitmentImage.getImage().getImageUrl();
        recruitmentImage.setImage(null);
        recruitmentImageRepository.delete(recruitmentImage);
        imageService.deleteImageByUrl(imageUrl);
        entityManager.flush();
    }
}
