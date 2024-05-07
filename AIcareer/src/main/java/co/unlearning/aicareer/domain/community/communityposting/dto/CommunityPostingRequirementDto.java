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
        @Schema(description = "블라인드 여부, null값 아닐시 admin 체크이므로 관리자만 값 수정")
        private Boolean isView;
    }
}