package co.unlearning.aicareer.domain.board.dto;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.domain.board.Board;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BoardResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardInfo {
        @Schema(description = "배너 이미지 url")
        private ImageResponseDto.ImageData bannerImage;
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "연결 페이지 url")
        private String pageLinkUrl;
        @Schema(description = "업로드 시간")
        private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
        @Schema(description = "최종 수정일")
        private String lastModified; // 최종 변경일
        @Schema(description = "제목")
        private String title; //제목
        @Schema(description = "내용")
        private String content; //내용

        public static BoardInfo of(Board board) {
            return BoardInfo.builder()
                    .bannerImage(ImageResponseDto.ImageData.of( board.getBannerImage()))
                    .uid(board.getUid())
                    .pageLinkUrl(board.getPageLinkUrl())
                    .uploadDate(board.getUploadDate())
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(board.getLastModified()))
                    .title(board.getTitle())
                    .content(board.getContent())
                    .build();
        }

        public static List<ImageResponseDto.ImageData> of(List<Image> images) {
            return images.stream().map(ImageResponseDto.ImageData::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardSimple {
        @Schema(description = "배너 이미지 url")
        private ImageResponseDto.ImageData bannerImage;
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "연결 페이지 url")
        private String pageLinkUrl;

        public static BoardSimple of(Board board) {
            return BoardSimple.builder()
                    .bannerImage(ImageResponseDto.ImageData.of( board.getBannerImage()))
                    .uid(board.getUid())
                    .pageLinkUrl(board.getPageLinkUrl())
                    .build();
        }

        public static List<BoardSimple> of(List<Board> boards) {
            return boards.stream().map(BoardSimple::of).collect(Collectors.toList());
        }
    }
}
