package co.unlearning.aicareer.domain.board.service;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.board.Board;
import co.unlearning.aicareer.domain.board.dto.BoardRequirementDto;
import co.unlearning.aicareer.domain.board.repository.BoardRepository;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ImageRepository imageRepository;
    public Board addBoardPost(BoardRequirementDto.BoardPost boardPost) {
        Image image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(boardPost.getBannerImage())).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
        );
        return boardRepository.save(Board.builder()
                        .pageLinkUrl(boardPost.getPageLink())
                        .bannerImage(image)
                        .title(boardPost.getTitle())
                        .uid(UUID.randomUUID().toString())
                        .content(boardPost.getContent())
                        .isView(true)
                .build());
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
        boardRepository.delete(getBoardByUid(uid));
    }
}
