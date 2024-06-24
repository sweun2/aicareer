package co.unlearning.aicareer.domain.community.communityvote.dto;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import co.unlearning.aicareer.domain.community.communityvote.VoteOption;
import co.unlearning.aicareer.global.utils.ApplicationContextUtil;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommunityVoteResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityVoteInfo {
        private String title;
        private String description;
        private Boolean isMultiple;
        private Boolean isAnonymous;
        private String endDate;
        private List<VoteOptionInfo> voteOptionInfos;
        private Integer communityPostingUid;
        public static CommunityVoteInfo of(CommunityVote communityVote) {
            UserService userService = ApplicationContextUtil.getBean(UserService.class);
            User loginUser;
            if(userService.isLogin()) {
                 loginUser = userService.getLoginUser(); // 로그인 유저 정보 가져오기
            } else {
                loginUser = null;
            }

            CommunityVoteInfoBuilder builder = CommunityVoteInfo.builder()
                    .title(communityVote.getTitle())
                    .description(communityVote.getDescription())
                    .isMultiple(communityVote.getIsMultiple())
                    .isAnonymous(communityVote.getIsAnonymous())
                    .endDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityVote.getEndDate()))
                    .voteOptionInfos(communityVote.getVoteOption().stream().map(voteOption -> {
                        VoteOptionInfo.VoteOptionInfoBuilder voteOptionInfoBuilder = VoteOptionInfo.builder()
                                .voteOption(voteOption.getOption())
                                .isVoted(false);
                        if(loginUser != null) {
                            voteOptionInfoBuilder.userSimple(UserResponseDto.UserSimple.of(loginUser));
                        }
                        return voteOptionInfoBuilder.build();
                    }).collect(Collectors.toList()));

            return builder.build();
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteOptionInfo {
        private Integer voteOptionId;
        private String voteOption;
        private Boolean isVoted;
        private UserResponseDto.UserSimple userSimple;
    }
}
