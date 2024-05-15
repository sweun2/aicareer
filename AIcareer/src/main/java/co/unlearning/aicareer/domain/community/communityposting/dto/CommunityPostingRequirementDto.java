package co.unlearning.aicareer.domain.community.communityposting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

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

        @Schema(description = "메인 이미지 url")
        private String mainImage;
        @Schema(description = "서브 이미지 url")
        private List<String> subImage;
    }
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class CommunityPostingIsView {
        @Schema(description = "커뮤니티 글 uid")
        private String uid;
        @Schema(description = "블라인드 여부, null값 아닐시 admin 체크이므로 관리자만 값 수정")
        private Boolean isView;
    }
}