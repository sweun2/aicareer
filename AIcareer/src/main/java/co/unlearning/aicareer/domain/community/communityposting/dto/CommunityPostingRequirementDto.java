package co.unlearning.aicareer.domain.community.communityposting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class CommunityPostingRequirementDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class CommunityPostingPost {
        @Schema(description = "제목")
        private String title;
        @Schema(description = "내용")
        private String content;
    }
}