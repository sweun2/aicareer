package co.unlearning.aicareer.domain.job.board.dto;

import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BoardResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardInfo {
        @Schema(description = "데스크탑 배너 이미지 url")
        private String desktopBannerImage;
        @Schema(description = "모바일 배너 이미지 url")
        private String mobileBannerImage;
        @Schema(description = "서브 이미지 url")
        private List<String> subImages;
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "연결 페이지 url")
        private String pageLinkUrl;
        @Schema(description = "업로드 시간")
        private String uploadDate;
        @Schema(description = "최종 수정일")
        private String lastModified; // 최종 변경일
        @Schema(description = "제목")
        private String title; //제목
        @Schema(description = "내용")
        private String content; //내용

        public static BoardInfo of(Board board) {
            BoardInfoBuilder builder = BoardInfo.builder()
                    .uid(board.getUid())
                    .pageLinkUrl(board.getPageLinkUrl())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(board.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(board.getLastModified()))
                    .title(board.getTitle())
                    .content(board.getContent());

            if (board.getDesktopBannerImage() != null) {
                builder.desktopBannerImage(ImagePathLengthConverter.extendImagePathLength(board.getDesktopBannerImage().getImage().getImageUrl()));
            } else builder.desktopBannerImage(StringUtils.EMPTY);
            if(board.getMobileBannerImage() != null) {
                builder.mobileBannerImage(ImagePathLengthConverter.extendImagePathLength(board.getMobileBannerImage().getImage().getImageUrl()));
            } else builder.mobileBannerImage(StringUtils.EMPTY);
            if(!board.getSubImages().isEmpty()) {
                builder.subImages(
                        board.getSubImages().stream()
                                .filter(boardImage -> boardImage.getImageOrder() != null && boardImage.getImageOrder() != 0)
                                .sorted(Comparator.comparingInt(BoardImage::getImageOrder))
                                .map(boardImage -> ImagePathLengthConverter.extendImagePathLength(boardImage.getImage().getImageUrl()))
                                .collect(Collectors.toList())
                );
            } else builder.subImages(new ArrayList<>());

            return builder.build();
        }

        public static List<BoardInfo> of(List<Board> boards) {
            return boards.stream().map(BoardInfo::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardSimple {
        @Schema(description = "배너 이미지 url")
        private String desktopBannerImage;
        @Schema(description = "모바일 배너 이미지 url")
        private String mobileBannerImage;
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "연결 페이지 url")
        private String pageLinkUrl;

        public static BoardSimple of(Board board) {
            BoardSimpleBuilder builder =  BoardSimple.builder()
                    .uid(board.getUid())
                    .pageLinkUrl(board.getPageLinkUrl());

            if (board.getDesktopBannerImage() != null) {
                builder.desktopBannerImage(ImagePathLengthConverter.extendImagePathLength(board.getDesktopBannerImage().getImage().getImageUrl()));
            } else builder.desktopBannerImage(StringUtils.EMPTY);
            if(board.getMobileBannerImage() != null) {
                builder.mobileBannerImage(ImagePathLengthConverter.extendImagePathLength(board.getMobileBannerImage().getImage().getImageUrl()));
            } else builder.mobileBannerImage(StringUtils.EMPTY);

            return builder.build();
        }

        public static List<BoardSimple> of(List<Board> boards) {
            return boards.stream().map(BoardSimple::of).collect(Collectors.toList());
        }
    }
}
