package co.unlearning.aicareer.domain.community.communitypostingimage.service;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitypostingimage.repository.CommunityPostingImageRepository;
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
    public CommunityPostingImage addNewCommunityImage(Image image, CommunityPosting communityPosting) {
        Optional<CommunityPostingImage> communityPostingImage = communityPostingImageRepository.findFirstByCommunityPostingOrderByImageOrderDesc(communityPosting);
        return communityPostingImage.map(postingImage -> communityPostingImageRepository.save(
                CommunityPostingImage.builder()
                        .image(image)
                        .communityPosting(communityPosting)
                        .imageOrder(postingImage.getImageOrder() + 1)
                        .build()
        )).orElseGet(() -> communityPostingImageRepository.save(
                CommunityPostingImage.builder()
                        .image(image)
                        .communityPosting(communityPosting)
                        .imageOrder(1)
                        .build()
        ));
    }
}
