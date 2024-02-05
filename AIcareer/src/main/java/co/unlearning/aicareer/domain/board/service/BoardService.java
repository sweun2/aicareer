package co.unlearning.aicareer.domain.board.service;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.Image.service.ImageService;
import co.unlearning.aicareer.domain.board.Board;
import co.unlearning.aicareer.domain.board.dto.BoardRequirementDto;
import co.unlearning.aicareer.domain.board.repository.BoardRepository;
import co.unlearning.aicareer.domain.sitemap.service.SiteMapService;
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
    private final BoardRepository boardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final SiteMapService siteMapService;
    public Board addBoardPost(BoardRequirementDto.BoardPost boardPost) {
        Image image = null;
        if(boardPost.getBannerImage()!=null) {
            image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }

        Board board = Board.builder()
                .pageLinkUrl(boardPost.getPageLink())
                .bannerImage(image)
                .title(boardPost.getTitle())
                .uid(UUID.randomUUID().toString())
                .content(boardPost.getContent())
                .lastModified(LocalDateTime.now())
                .isView(true)
                .build();

        image.setBoard(board);
        if(!boardPost.getSubImage().isEmpty()) {
            Set<Image> subImages = new HashSet<>();
            for (String subImageUrl : boardPost.getSubImage()) {
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setBoard(board);
                subImages.add(subImage);
            }
            board.setSubImageSet(subImages);
        }
        boardRepository.save(board);

        siteMapService.registerBoardSiteMap(board);
        return board;
    }
    public Board updateBoardPost(String boardUid, BoardRequirementDto.BoardPost boardPost) {
        Board board = boardRepository.findByUid(boardUid).orElseThrow(
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
                subImage.setBoard(board);
                subImages.add(subImage);
            }
            board.setSubImageSet(subImages);
        }


        board.setPageLinkUrl(boardPost.getPageLink());
        board.setBannerImage(image);
        board.setTitle(boardPost.getTitle());
        board.setContent(boardPost.getContent());
        board.setLastModified(LocalDateTime.now());

        boardRepository.save(board);
        siteMapService.registerBoardSiteMap(board);
        return board;
    }
    public List<Board> getBoardList() {
        return boardRepository.findAllByIsViewIsTrue();
    }
    public Board getBoardByUid(String uid) {
        return boardRepository.findByUid(uid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public void removeBoardByUid(String uid) {
        Board board = getBoardByUid(uid);
        imageService.deleteImage(board.getBannerImage().getImageUrl());
        board.getSubImageSet().forEach(
                image -> {
                    imageService.deleteImage(image.getImageUrl());
                }
        );

        siteMapService.deleteSiteMap(board);
        boardRepository.delete(board);
    }
}
