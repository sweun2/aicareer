package co.unlearning.aicareer.domain.community.communitycommentuser.dto;

import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class CommunityCommentUserResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityCommentUserInfo {
        @Schema(description = "댓글 uid")
        private String commentUid;
        @Schema(description = "유저 정보")
        private UserResponseDto.UserSimple userSimple;
        @Column
        private Boolean isReport;
        @Column
        private Boolean isRecommend;

        public static CommunityCommentUserInfo of(CommunityCommentUser communityCommentUser) {
            return CommunityCommentUserInfo.builder()
                    .commentUid(communityCommentUser.getCommunityComment().getUid())
                    .userSimple(UserResponseDto.UserSimple.of(communityCommentUser.getUser()))
                    .isRecommend(communityCommentUser.getIsRecommend())
                    .isReport(communityCommentUser.getIsReport())
                    .build();
        }

        public static List<CommunityCommentUserInfo> of(List<CommunityCommentUser> communityCommentUsers) {
            return communityCommentUsers.stream().map(CommunityCommentUserInfo::of).collect(Collectors.toList());
        }
    }
}
