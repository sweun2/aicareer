package co.unlearning.aicareer.domain.job.boardimage.service;

import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
import co.unlearning.aicareer.domain.job.boardimage.repository.BoardImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardImageService {
    private final BoardImageRepository boardImageRepository;
    private final ImageService imageService;
    private final EntityManager entityManager;

    public void removeBoardImage(BoardImage boardImage) {
        String imageUrl = boardImage.getImage().getImageUrl();

        boardImage.setImage(null);
        boardImageRepository.delete(boardImage);
        imageService.deleteImageByUrl(imageUrl);
        entityManager.flush();
    }
}
