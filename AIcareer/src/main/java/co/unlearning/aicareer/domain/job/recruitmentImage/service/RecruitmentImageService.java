package co.unlearning.aicareer.domain.job.recruitmentImage.service;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitmentImage.RecruitmentImage;
import co.unlearning.aicareer.domain.job.recruitmentImage.repository.RecruitmentImageRepository;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public RecruitmentImage addNewRecruitmentImage(Image image, Recruitment recruitment) {
        RecruitmentImage recruitmentImage = recruitmentImageRepository.findFirstByRecruitmentOrderByImageOrderDesc(recruitment).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.INVALID_RECRUITMENT_IMAGE)
        );
        return recruitmentImageRepository.save(
                RecruitmentImage.builder()
                        .image(image)
                        .recruitment(recruitment)
                        .imageOrder(recruitmentImage.getImageOrder()+1)
                        .build()
        );
    }
}
