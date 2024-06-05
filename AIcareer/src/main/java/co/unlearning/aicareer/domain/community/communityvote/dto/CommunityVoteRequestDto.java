package co.unlearning.aicareer.domain.community.communityvote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class CommunityVoteRequestDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class VotePost {
        @Schema(description = "제목")
        private String title;
        @Schema(description = "설명")
        private String description;
        @Schema(description = "다중 선택 여부")
        private Boolean isMultiple;
        @Schema(description = "익명 여부")
        private Boolean isAnonymous;
        @Schema(description = "종료일")
        private String endDate;
        @Schema(description = "투표 선택지, 최소 2개 이상, 최대 10개 이하, 중복 불가")
        private List<String> voteOption;
        @Schema(description = "커뮤니티 글 uid")
        private String postingUid;
    }
}
