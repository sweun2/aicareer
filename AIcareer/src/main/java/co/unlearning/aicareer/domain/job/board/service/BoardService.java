package co.unlearning.aicareer.domain.job.board.service;

import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.job.board.dto.BoardRequirementDto;
import co.unlearning.aicareer.domain.job.board.repository.BoardRepository;
import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.common.sitemap.service.SiteMapService;
import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
import co.unlearning.aicareer.domain.job.boardimage.repository.BoardImageRepository;
import co.unlearning.aicareer.domain.job.boardimage.service.BoardImageService;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final SiteMapService siteMapService;
    private final BoardImageRepository boardImageRepository;
    private final BoardImageService boardImageService;
    @PersistenceContext
    private final EntityManager entityManager;
    public Board addBoardPost(BoardRequirementDto.BoardPost boardPost) {
        Board board = Board.builder()
                .pageLinkUrl(boardPost.getPageLink())
                .title(boardPost.getTitle())
                .uid(UUID.randomUUID().toString())
                .content(boardPost.getContent())
                .lastModified(LocalDateTime.now())
                .subImages(new ArrayList<>())
                .isView(true)
                .build();
        Image desktopImage;
        if(boardPost.getDesktopBannerImage()!=null && !boardPost.getDesktopBannerImage().equals(StringUtils.EMPTY)) {
            desktopImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getDesktopBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
            desktopImage.setIsRelated(true);

            BoardImage boardDesktopImage = BoardImage.builder()
                    .image(desktopImage)
                    .board(board)
                    .imageOrder(0)
                    .build();

            board.setDesktopBannerImage(boardDesktopImage);
        }
        if(boardPost.getMobileBannerImage()!=null && !boardPost.getMobileBannerImage().equals(StringUtils.EMPTY)) {
            Image mobileImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getMobileBannerImage())).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
            mobileImage.setIsRelated(true);

            BoardImage boardMobileImage = BoardImage.builder()
                    .image(mobileImage)
                    .board(board)
                    .imageOrder(0)
                    .build();

            board.setMobileBannerImage(boardMobileImage);
        }
        if(!boardPost.getSubImage().isEmpty()) {
            List<BoardImage> boardImages = new ArrayList<>();
            int order = 0;
            for (String subImageUrl : boardPost.getSubImage()) {
                order += 1;
                Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
                );
                subImage.setIsRelated(true);

                boardImages.add(BoardImage.builder()
                        .image(subImage)
                        .board(board)
                        .imageOrder(order)
                        .build());
            }
            board.setSubImages(boardImages);
        }
        boardRepository.save(board);

        siteMapService.registerBoardSiteMap(board);
        return board;
    }
    public Board updateBoardPost(String boardUid, BoardRequirementDto.BoardPost boardPost) {
        Board board = boardRepository.findByUid(boardUid).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );

        updateSubImages(board, boardPost.getSubImage());
        // 배너 이미지 업데이트
        updateBannerImage(boardPost.getDesktopBannerImage(), board.getDesktopBannerImage(), board::setDesktopBannerImage);
        updateBannerImage(boardPost.getMobileBannerImage(), board.getMobileBannerImage(), board::setMobileBannerImage);

        // 기타 정보 업데이트
        board.setPageLinkUrl(boardPost.getPageLink());
        board.setTitle(boardPost.getTitle());
        board.setContent(boardPost.getContent());
        board.setLastModified(LocalDateTime.now());


        boardRepository.save(board);
        siteMapService.registerBoardSiteMap(board);
        return board;
    }
    public void updateBannerImage(String newImageUrl, BoardImage currentBoardImage, Consumer<BoardImage> setBoardImage) {
        if (newImageUrl != null && !newImageUrl.isEmpty()) {
            Image newImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(newImageUrl)).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
            if (currentBoardImage != null) {
                if (!newImage.getImageUrl().equals(currentBoardImage.getImage().getImageUrl())) {
                    currentBoardImage.getImage().setIsRelated(false);
                    boardImageService.removeBoardImage(currentBoardImage);


                    // 새로운 BoardImage를 설정합니다.
                    BoardImage newBoardImage = BoardImage.builder()
                            .imageOrder(0)
                            .image(newImage)
                            .board(currentBoardImage.getBoard())
                            .build();
                    newImage.setIsRelated(true);
                    setBoardImage.accept(newBoardImage);
                    boardImageRepository.save(newBoardImage); // 새로운 이미지를 저장
                }
            }
        } else if (currentBoardImage != null) {
            // 새로운 이미지 URL이 없고 현재 이미지가 있는 경우, 현재 BoardImage를 삭제합니다.
            currentBoardImage.getImage().setIsRelated(false);
            boardImageService.removeBoardImage(currentBoardImage);
            setBoardImage.accept(null);
            entityManager.flush(); // 삭제 작업을 즉시 반영
        }
    }


    public void updateSubImages(Board board, List<String> newSubImageUrls) {
        // 현재 순서가 0이 아닌 서브 이미지를 삭제
        List<BoardImage> currentBoardImages = board.getSubImages().stream()
                .filter(boardImage -> boardImage.getImageOrder() != null && boardImage.getImageOrder() != 0)
                .collect(Collectors.toList());

        if (!currentBoardImages.isEmpty()) {
            currentBoardImages.forEach(bi->{
                bi.getImage().setIsRelated(false);
                imageService.deleteImageByUrl(bi.getImage().getImageUrl());
            });
            boardImageRepository.deleteAll(currentBoardImages);
            entityManager.flush(); // 삭제된 내용을 DB에 반영하도록 플러시
        }

        // 현재 서브 이미지를 보드에서 제거
        board.getSubImages().removeAll(currentBoardImages);
        board.getSubImages().forEach(boardImage -> log.info(boardImage.getImage().getImageUrl()));
        log.info("test");
        boardRepository.save(board); // 보드를 저장하여 상태를 반영

        // 새로운 서브 이미지를 생성하고 추가
        for (int order = 0; order < newSubImageUrls.size(); order++) {
            String imageUrl = newSubImageUrls.get(order);
            String slicedImageUrl = ImagePathLengthConverter.slicingImagePathLength(imageUrl);
            Image image = imageRepository.findByImageUrl(slicedImageUrl).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
            Optional<BoardImage> boardImageOptional = boardImageRepository.findByImage(image);
            if(boardImageOptional.isEmpty()) {
                BoardImage newBoardImage = new BoardImage();
                newBoardImage.setBoard(board);
                image.setIsRelated(true);
                newBoardImage.setImage(image);
                newBoardImage.setImageOrder(order + 1);

                board.getSubImages().add(newBoardImage);
                boardImageRepository.save(newBoardImage); // 각 새로운 보드 이미지를 개별적으로 저장
            } else {
                boardImageOptional.get().setImageOrder(order+1);
                boardImageRepository.save(boardImageOptional.get());
            }
        }

        boardRepository.save(board); // 변경 사항을 반영하기 위해 보드를 저장
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
        BoardImage t1 = board.getDesktopBannerImage();
        BoardImage t2 = board.getMobileBannerImage();

/*        boardImageService.removeBoardImage(t1);
        if(t2!=null)
            boardImageService.removeBoardImage(t2);*/

        if(!board.getSubImages().isEmpty())
            board.getSubImages().forEach(
                    boardImage -> {
                        if(boardImage!=null) {
                            if(boardImage.getImage()!=null) {
                                boardImage.getImage().setIsRelated(false);
                                imageRepository.save(boardImage.getImage());
                            }
                            boardImageService.removeBoardImage(boardImage);
                        }
                    });
        siteMapService.deleteSiteMap(board);
        boardRepository.delete(board);
    }
}
