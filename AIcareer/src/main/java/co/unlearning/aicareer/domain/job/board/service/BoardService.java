package co.unlearning.aicareer.domain.job.board.service;

import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.job.board.dto.JobBoardRequirementDto;
import co.unlearning.aicareer.domain.job.board.repository.JobBoardRepository;
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
public class BoardService {
    private final JobBoardRepository jobBoardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final SiteMapService siteMapService;
    public Board addBoardPost(JobBoardRequirementDto.BoardPost boardPost) {
        Image desktopImage = null;
        if(boardPost.getDesktopBannerImage()!=null) {
            desktopImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getDesktopBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }
        assert desktopImage != null;
        desktopImage.setIsRelated(true);

        Image mobileImage = null;
        if(boardPost.getMobileBannerImage()!=null) {
            mobileImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getMobileBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }
        assert mobileImage != null;
        mobileImage.setIsRelated(true);


        Board board = Board.builder()
                .pageLinkUrl(boardPost.getPageLink())
                .bannerImage(desktopImage)
                .mobileBannerImage(mobileImage)
                .title(boardPost.getTitle())
                .uid(UUID.randomUUID().toString())
                .content(boardPost.getContent())
                .lastModified(LocalDateTime.now())
                .isView(true)
                .build();

        desktopImage.setBoard(board);
        mobileImage.setBoard(board);
        if(!boardPost.getSubImage().isEmpty()) {
            Set<Image> subImages = new HashSet<>();
            for (String subImageUrl : boardPost.getSubImage()) {
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setIsRelated(true);
                subImage.setBoard(board);
                subImages.add(subImage);
            }
            board.setSubImageSet(subImages);
        }
        //board.setBoardContentType(boardPost.getContentType());
        jobBoardRepository.save(board);

        siteMapService.registerJobBoardSiteMap(board);
        return board;
    }
    public Board updateBoardPost(String boardUid, JobBoardRequirementDto.BoardPost boardPost) {
        Board board = jobBoardRepository.findByUid(boardUid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        Image image = null;
        if(boardPost.getDesktopBannerImage()!=null) {
            image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getDesktopBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }
        if(!boardPost.getSubImage().isEmpty()) {
            Set<Image> subImages = new HashSet<>();
            for (String subImageUrl : boardPost.getSubImage()) {
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setBoard(board);
                subImages.add(subImage);
            }
            board.getSubImageSet().clear();
            board.getSubImageSet().addAll(subImages);
        }


        board.setPageLinkUrl(boardPost.getPageLink());
        board.setBannerImage(image);
        board.setTitle(boardPost.getTitle());
        board.setContent(boardPost.getContent());
        board.setLastModified(LocalDateTime.now());
        //board.setBoardContentType(boardPost.getContentType());

        jobBoardRepository.save(board);
        siteMapService.registerJobBoardSiteMap(board);
        return board;
    }
    public List<Board> getBoardList() {
        return jobBoardRepository.findAllByIsViewIsTrue();
    }
    public Board getBoardByUid(String uid) {
        return jobBoardRepository.findByUid(uid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public void removeBoardByUid(String uid) {
        Board board = getBoardByUid(uid);
        imageService.deleteImage(board.getBannerImage().getImageUrl());
        board.getSubImageSet().forEach(
                image -> imageService.deleteImage(image.getImageUrl())
        );

        siteMapService.deleteSiteMap(board);
        jobBoardRepository.delete(board);
    }
}
