package co.unlearning.aicareer.domain.community.communitypostingimage.service;

import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitypostingimage.repository.CommunityPostingImageRepository;
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
public class CommunityPostingImageService {
    private final ImageService imageService;
    private final EntityManager entityManager;
    private final CommunityPostingImageRepository communityPostingImageRepository;
    public void removeCommunityPostingImage(CommunityPostingImage communityPostingImage) {
        String imageUrl = communityPostingImage.getImage().getImageUrl();
        communityPostingImage.setImage(null);
        communityPostingImageRepository.delete(communityPostingImage);
        imageService.deleteImageByUrl(imageUrl);
        entityManager.flush();
    }
}
