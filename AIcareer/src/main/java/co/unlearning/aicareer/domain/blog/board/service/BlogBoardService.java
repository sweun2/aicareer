package co.unlearning.aicareer.domain.blog.board.service;

import co.unlearning.aicareer.domain.blog.board.BlogBoard;
import co.unlearning.aicareer.domain.blog.board.dto.BlogBoardRequirementDto;
import co.unlearning.aicareer.domain.blog.board.repository.BlogBoardRepository;
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
public class BlogBoardService {
    private final BlogBoardRepository blogBoardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final SiteMapService siteMapService;
    public BlogBoard addBoardPost(BlogBoardRequirementDto.BoardPost boardPost) {
        Image image = null;
        if(boardPost.getBannerImage()!=null) {
            image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }

        BlogBoard blogBoard = BlogBoard.builder()
                .pageLinkUrl(boardPost.getPageLink())
                .bannerImage(image)
                .title(boardPost.getTitle())
                .uid(UUID.randomUUID().toString())
                .content(boardPost.getContent())
                .lastModified(LocalDateTime.now())
                .isView(true)
                .build();

        assert image != null;
        image.setBlogBoard(blogBoard);
        if(!boardPost.getSubImage().isEmpty()) {
            Set<Image> subImages = new HashSet<>();
            for (String subImageUrl : boardPost.getSubImage()) {
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setBlogBoard(blogBoard);
                subImages.add(subImage);
            }
            blogBoard.setSubImageSet(subImages);
        }
        blogBoardRepository.save(blogBoard);

        siteMapService.registerBlogBoardSiteMap(blogBoard);
        return blogBoard;
    }
    public BlogBoard updateBoardPost(String boardUid, BlogBoardRequirementDto.BoardPost boardPost) {
        BlogBoard blogBoard = blogBoardRepository.findByUid(boardUid).orElseThrow(
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
                subImage.setBlogBoard(blogBoard);
                subImages.add(subImage);
            }
            blogBoard.getSubImageSet().clear();
            blogBoard.getSubImageSet().addAll(subImages);
        }


        blogBoard.setPageLinkUrl(boardPost.getPageLink());
        blogBoard.setBannerImage(image);
        blogBoard.setTitle(boardPost.getTitle());
        blogBoard.setContent(boardPost.getContent());
        blogBoard.setLastModified(LocalDateTime.now());

        blogBoardRepository.save(blogBoard);
        siteMapService.registerBlogBoardSiteMap(blogBoard);
        return blogBoard;
    }
    public List<BlogBoard> getBoardList() {
        return blogBoardRepository.findAllByIsViewIsTrue();
    }
    public BlogBoard getBoardByUid(String uid) {
        return blogBoardRepository.findByUid(uid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public void removeBoardByUid(String uid) {
        BlogBoard blogBoard = getBoardByUid(uid);
        imageService.deleteImage(blogBoard.getBannerImage().getImageUrl());
        blogBoard.getSubImageSet().forEach(
                image -> imageService.deleteImage(image.getImageUrl())
        );

        siteMapService.deleteSiteMap(blogBoard);
        blogBoardRepository.delete(blogBoard);
    }
}
