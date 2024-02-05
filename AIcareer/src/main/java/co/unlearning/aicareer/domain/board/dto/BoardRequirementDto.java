package co.unlearning.aicareer.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class BoardRequirementDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class BoardPost {
        @Schema(description = "배너 이미지 url")
        private String bannerImage;
        @Schema(description = "서브 이미지 url")
        private List<String> subImage;
        @Schema(description = "페이지 링크 url")
        private String pageLink;
        @Schema(description = "제목")
        private String title;
        @Schema(description = "내용")
        private String content;
    }
}