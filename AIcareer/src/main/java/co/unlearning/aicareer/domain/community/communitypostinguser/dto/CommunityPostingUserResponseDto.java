package co.unlearning.aicareer.domain.community.communitypostinguser.dto;

import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class CommunityPostingUserResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityPostingUserInfo {
        @Schema(description = "게시글 uid")
        private String postingUid;
        @Schema(description = "유저 정보")
        private UserResponseDto.UserSimple userSimple;
        @Column
        private Boolean isReport;
        @Column
        private Boolean isRecommend;

        public static CommunityPostingUserInfo of(CommunityPostingUser communityPostingUser) {
            return CommunityPostingUserInfo.builder()
                    .postingUid(communityPostingUser.getCommunityPosting().getUid())
                    .userSimple(UserResponseDto.UserSimple.of(communityPostingUser.getUser()))
                    .isRecommend(communityPostingUser.getIsRecommend())
                    .isReport(communityPostingUser.getIsReport())
                    .build();
        }

        public static List<CommunityPostingUserInfo> of(List<CommunityPostingUser> communityPostingUsers) {
            return communityPostingUsers.stream().map(CommunityPostingUserInfo::of).collect(Collectors.toList());
        }
    }
}
