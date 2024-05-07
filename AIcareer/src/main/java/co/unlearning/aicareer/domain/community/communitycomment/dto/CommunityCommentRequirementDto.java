package co.unlearning.aicareer.domain.community.communitycomment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class CommunityCommentRequirementDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class CommunityCommentPost {
        @Schema(description = "게시글 uid")
        private String postingUid;
        @Schema(description = "댓글 내용")
        private String content;
        @Schema(description = "블라인드 여부, null값 아닐시 admin 체크이므로 관리자만 값 수정")
        private Boolean isView;
    }
}