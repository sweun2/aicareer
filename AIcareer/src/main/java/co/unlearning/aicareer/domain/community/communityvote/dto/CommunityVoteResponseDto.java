package co.unlearning.aicareer.domain.community.communityvote.dto;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import co.unlearning.aicareer.domain.community.communityvote.VoteUser;
import co.unlearning.aicareer.global.utils.ApplicationContextUtil;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class CommunityVoteResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityVoteInfo {
        private Integer id;
        private String title;
        private String description;
        private Boolean isMultiple;
        private Boolean isAnonymous;
        private String endDate;
        private List<VoteOptionInfo> voteOptionInfos;
        public static CommunityVoteInfo of(CommunityVote communityVote) {
            UserService userService = ApplicationContextUtil.getBean(UserService.class);
            User loginUser;
            if(userService.isLogin()) {
                 loginUser = userService.getLoginUser(); // 로그인 유저 정보 가져오기
            } else {
                loginUser = null;
            }

            CommunityVoteInfoBuilder builder = CommunityVoteInfo.builder()
                    .id(communityVote.getId())
                    .title(communityVote.getTitle())
                    .description(communityVote.getDescription())
                    .isMultiple(communityVote.getIsMultiple())
                    .isAnonymous(communityVote.getIsAnonymous())
                    .endDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityVote.getEndDate()))
                    .voteOptionInfos(communityVote.getVoteOption().stream().map(voteOption -> {
                        VoteOptionInfo.VoteOptionInfoBuilder voteOptionInfoBuilder = VoteOptionInfo.builder()
                                .voteOptionId(voteOption.getId())
                                .voteOption(voteOption.getOption())
                                .isVoted(communityVote.getVoteUser().stream().anyMatch(voteUser -> voteUser.getUser().equals(loginUser) && voteUser.getVoteOption().equals(voteOption)))
                                .userSimpleList(communityVote.getIsAnonymous() ? null : voteOption.getVoteUserSet().stream().map(VoteUser::getUser).map(UserResponseDto.UserSimple::of).collect(Collectors.toList()));
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
        private Integer voteCnt;
        private Boolean isVoted;
        private List<UserResponseDto.UserSimple> userSimpleList;
    }
}
