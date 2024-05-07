package co.unlearning.aicareer.domain.job.board.dto;

import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.common.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobBoardResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardInfo {
        @Schema(description = "데스크탑 배너 이미지 url")
        private ImageResponseDto.ImageData desktopBannerImage;
        @Schema(description = "모바일 배너 이미지 url")
        private ImageResponseDto.ImageData mobileBannerImage;
        @Schema(description = "서브 이미지 url")
        private List<ImageResponseDto.ImageData> subImages;
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
       /* @Schema(description = "내부 타입",allowableValues = {"markdown,html"})
        private String contentType;*/

        public static BoardInfo of(Board board) {
            BoardInfoBuilder builder =BoardInfo.builder()
                    .subImages(ImageResponseDto.ImageData.of(new ArrayList<>(board.getSubImageSet())))
                    .uid(board.getUid())
                    .pageLinkUrl(board.getPageLinkUrl())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(board.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(board.getLastModified()))
                    .title(board.getTitle())
                    .content(board.getContent());

            if (board.getBannerImage() != null) {
                builder.desktopBannerImage(ImageResponseDto.ImageData.of(board.getBannerImage()));
            }
            if(board.getMobileBannerImage() != null) {
                builder.mobileBannerImage(ImageResponseDto.ImageData.of(board.getMobileBannerImage()));
            }
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
        private ImageResponseDto.ImageData desktopBannerImage;
        @Schema(description = "모바일 배너 이미지 url")
        private ImageResponseDto.ImageData mobileBannerImage;
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "연결 페이지 url")
        private String pageLinkUrl;

        public static BoardSimple of(Board board) {
            BoardSimpleBuilder builder =  BoardSimple.builder()
                    .uid(board.getUid())
                    .pageLinkUrl(board.getPageLinkUrl());

            if (board.getBannerImage() != null) {
                builder.desktopBannerImage(ImageResponseDto.ImageData.of(board.getBannerImage()));
            }
            if(board.getMobileBannerImage() != null) {
                builder.mobileBannerImage(ImageResponseDto.ImageData.of(board.getMobileBannerImage()));
            }
            return builder.build();

        }

        public static List<BoardSimple> of(List<Board> boards) {
            return boards.stream().map(BoardSimple::of).collect(Collectors.toList());
        }
    }
}
