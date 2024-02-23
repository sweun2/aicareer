package co.unlearning.aicareer.domain.job.jobboard.dto;

import co.unlearning.aicareer.domain.job.jobboard.JobBoard;
import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
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
        @Schema(description = "배너 이미지 url")
        private ImageResponseDto.ImageData bannerImage;
        @Schema(description = "서브 이미지 url")
        private List<ImageResponseDto.ImageData> subImages;
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

        public static BoardInfo of(JobBoard jobBoard) {
            BoardInfoBuilder builder =BoardInfo.builder()
                    .subImages(ImageResponseDto.ImageData.of(new ArrayList<>(jobBoard.getSubImageSet())))
                    .uid(jobBoard.getUid())
                    .pageLinkUrl(jobBoard.getPageLinkUrl())
                    .uploadDate(jobBoard.getUploadDate())
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(jobBoard.getLastModified()))
                    .title(jobBoard.getTitle())
                    .content(jobBoard.getContent());

            if (jobBoard.getBannerImage() != null) {
                builder.bannerImage(ImageResponseDto.ImageData.of(jobBoard.getBannerImage()));
            }
            return builder.build();
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

        public static BoardSimple of(JobBoard jobBoard) {
            BoardSimpleBuilder builder =  BoardSimple.builder()
                    .uid(jobBoard.getUid())
                    .pageLinkUrl(jobBoard.getPageLinkUrl());

            if (jobBoard.getBannerImage() != null) {
                builder.bannerImage(ImageResponseDto.ImageData.of(jobBoard.getBannerImage()));
            }
            return builder.build();

        }

        public static List<BoardSimple> of(List<JobBoard> jobBoards) {
            return jobBoards.stream().map(BoardSimple::of).collect(Collectors.toList());
        }
    }
}
