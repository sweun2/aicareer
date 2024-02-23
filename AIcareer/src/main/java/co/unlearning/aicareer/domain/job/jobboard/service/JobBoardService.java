package co.unlearning.aicareer.domain.job.jobboard.service;

import co.unlearning.aicareer.domain.job.jobboard.JobBoard;
import co.unlearning.aicareer.domain.job.jobboard.dto.JobBoardRequirementDto;
import co.unlearning.aicareer.domain.job.jobboard.repository.JobBoardRepository;
import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.common.sitemap.service.SiteMapService;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class JobBoardService {
    private final JobBoardRepository jobBoardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final SiteMapService siteMapService;
    public JobBoard addBoardPost(JobBoardRequirementDto.BoardPost boardPost) {
        Image image = null;
        if(boardPost.getBannerImage()!=null) {
            image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }

        JobBoard jobBoard = JobBoard.builder()
                .pageLinkUrl(boardPost.getPageLink())
                .bannerImage(image)
                .title(boardPost.getTitle())
                .uid(UUID.randomUUID().toString())
                .content(boardPost.getContent())
                .lastModified(LocalDateTime.now())
                .isView(true)
                .build();

        assert image != null;
        image.setJobBoard(jobBoard);
        if(!boardPost.getSubImage().isEmpty()) {
            Set<Image> subImages = new HashSet<>();
            for (String subImageUrl : boardPost.getSubImage()) {
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setJobBoard(jobBoard);
                subImages.add(subImage);
            }
            jobBoard.setSubImageSet(subImages);
        }
        jobBoardRepository.save(jobBoard);

        siteMapService.registerJobBoardSiteMap(jobBoard);
        return jobBoard;
    }
    public JobBoard updateBoardPost(String boardUid, JobBoardRequirementDto.BoardPost boardPost) {
        JobBoard jobBoard = jobBoardRepository.findByUid(boardUid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        Image image = null;
        if(boardPost.getBannerImage()!=null) {
            image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }
        if(!boardPost.getSubImage().isEmpty()) {
            Set<Image> subImages = new HashSet<>();
            for (String subImageUrl : boardPost.getSubImage()) {
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setJobBoard(jobBoard);
                subImages.add(subImage);
            }
            jobBoard.getSubImageSet().clear();
            jobBoard.getSubImageSet().addAll(subImages);
        }


        jobBoard.setPageLinkUrl(boardPost.getPageLink());
        jobBoard.setBannerImage(image);
        jobBoard.setTitle(boardPost.getTitle());
        jobBoard.setContent(boardPost.getContent());
        jobBoard.setLastModified(LocalDateTime.now());

        jobBoardRepository.save(jobBoard);
        siteMapService.registerJobBoardSiteMap(jobBoard);
        return jobBoard;
    }
    public List<JobBoard> getBoardList() {
        return jobBoardRepository.findAllByIsViewIsTrue();
    }
    public JobBoard getBoardByUid(String uid) {
        return jobBoardRepository.findByUid(uid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public void removeBoardByUid(String uid) {
        JobBoard jobBoard = getBoardByUid(uid);
        imageService.deleteImage(jobBoard.getBannerImage().getImageUrl());
        jobBoard.getSubImageSet().forEach(
                image -> imageService.deleteImage(image.getImageUrl())
        );

        siteMapService.deleteSiteMap(jobBoard);
        jobBoardRepository.delete(jobBoard);
    }
}
