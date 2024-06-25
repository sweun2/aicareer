package co.unlearning.aicareer.domain.community.communityposting.dto;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.domain.community.communityvote.dto.CommunityVoteRequestDto;
import co.unlearning.aicareer.domain.community.communityvote.dto.CommunityVoteResponseDto;
import co.unlearning.aicareer.global.utils.ApplicationContextUtil;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommunityPostingResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityPostInfo {
        @Schema(description = "글 uid")
        private String uid;
        private String mainImageUrl;
        private List<String> imageUrls;
        @Schema(description = "업로드 시간")
        private String uploadDate;
        @Schema(description = "최종 수정일")
        private String lastModified; // 최종 변경일
        @Schema(description = "제목")
        private String title; //제목
        @Schema(description = "내용")
        private String content; //내용
        @Schema(description = "조회 수")
        private Integer hits; //조회수
        @Schema(description = "댓글 수")
        private Integer commentCnt;
        @Schema(description = "추천 수")
        private Integer reportCnt; //내용
        @Schema(description = "신고 수")
        private Integer recommendCnt; //내용
        @Schema(description = "볼 수 있는지 여부/ 신고 횟수 초과시 가려짐")
        private Boolean isView;
        @Schema(description = "익명 여부")
        private Boolean isAnonymous;
        @Schema(description = "글쓴이 정보")
        private UserResponseDto.UserSimple writer;
        @Schema(description = "로그인 유저의 정보")
        private CommunityPostingUserResponseDto.CommunityPostingUserInfo communityPostingUserInfo;
        @Schema(description = "투표 정보")
        private CommunityVoteResponseDto.CommunityVoteInfo communityVoteInfo;


        public static CommunityPostInfo of(Map.Entry<CommunityPosting,CommunityPostingUser> postingUserEntry) {
            UserService userService = ApplicationContextUtil.getBean(UserService.class);
            User loginUser;
            if(userService.isLogin()) {
                 loginUser = userService.getLoginUser(); // 로그인 유저 정보 가져오기
            } else {
                loginUser = null;
            }

            CommunityPosting communityPosting = postingUserEntry.getKey();
            CommunityPostingUser communityPostingUser = postingUserEntry.getValue();
            CommunityPostInfoBuilder builder = CommunityPostInfo.builder()
                    .uid(communityPosting.getUid())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityPosting.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(communityPosting.getLastModified()))
                    .title(communityPosting.getTitle())
                    .content(communityPosting.getContent())
                    .hits(communityPosting.getHits())
                    .commentCnt(communityPosting.getCommentCnt())
                    .reportCnt(communityPosting.getReportCnt())
                    .recommendCnt(communityPosting.getRecommendCnt())
                    .isView(communityPosting.getIsView())
                    .isAnonymous(communityPosting.getIsAnonymous())
                    .communityPostingUserInfo(CommunityPostingUserResponseDto.CommunityPostingUserInfo.of(communityPostingUser))
                    .communityVoteInfo(communityPosting.getCommunityVote() == null ? null : CommunityVoteResponseDto.CommunityVoteInfo.of(communityPosting.getCommunityVote()));
                    ;
            if (loginUser!=null && communityPosting.getWriter().getId().equals(loginUser.getId())) {
                builder.writer(UserResponseDto.UserSimple.of(communityPosting.getWriter()));
            } else {
                builder.writer(UserResponseDto.UserSimple.of(communityPosting.getIsAnonymous() ? null : communityPosting.getWriter()));
            }

            if(!communityPosting.getSubImages().isEmpty()) {
                builder.imageUrls(
                        communityPosting.getSubImages().stream()
                                .filter(recruitmentImage -> recruitmentImage.getImageOrder() != null && recruitmentImage.getImageOrder() != 0)
                                .sorted(Comparator.comparingInt(CommunityPostingImage::getImageOrder))
                                .map(recruitmentImage -> recruitmentImage.getImage().getImageUrl())
                                .map(ImagePathLengthConverter::extendImagePathLength)
                                .collect(Collectors.toList())
                );
                builder.mainImageUrl(
                        ImagePathLengthConverter.extendImagePathLength(
                        communityPosting.getSubImages().stream()
                        .filter(recruitmentImage->recruitmentImage.getImageOrder() == 1).toList().get(0)
                                .getImage().getImageUrl())
                );
            } else {
                builder.imageUrls(new ArrayList<>());
                builder.mainImageUrl(StringUtils.EMPTY);
            }

            return builder.build();
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityPostSimple {
        @Schema(description = "글 uid")
        private String uid;
        private String mainImageUrl;
        @Schema(description = "업로드 시간")
        private String uploadDate;
        @Schema(description = "최종 수정일")
        private String lastModified; // 최종 변경일
        @Schema(description = "제목")
        private String title; //제목
        @Schema(description = "내용")
        private String simpleContent; //내용
        @Schema(description = "조회 수")
        private Integer hits; //조회수
        @Schema(description = "댓글 수")
        private Integer commentCnt;
        @Schema(description = "추천 수")
        private Integer reportCnt; //내용
        @Schema(description = "신고 수")
        private Integer recommendCnt; //내용
        @Schema(description = "볼 수 있는지 여부/ 신고 횟수 초과시 가려짐")
        private Boolean isView;
        @Schema(description = "글쓴이 정보")
        private UserResponseDto.UserSimple writer;
        @Schema(description = "익명 여부")
        private Boolean isAnonymous;
        @Schema(description = "투표 정보")
        private CommunityVoteResponseDto.CommunityVoteInfo communityVoteInfo;

        public static CommunityPostSimple of(CommunityPosting communityPosting) {
            String simpleContent = communityPosting.getContent();
            int contentLength = 100;
            if (communityPosting.getContent().length() > contentLength) {
                simpleContent = simpleContent.substring(0, contentLength);
            }

            CommunityPostSimpleBuilder builder = CommunityPostSimple.builder()
                    .uid(communityPosting.getUid())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityPosting.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(communityPosting.getLastModified()))
                    .title(communityPosting.getTitle())
                    .simpleContent(simpleContent)
                    .hits(communityPosting.getHits())
                    .commentCnt(communityPosting.getCommentCnt())
                    .reportCnt(communityPosting.getReportCnt())
                    .recommendCnt(communityPosting.getRecommendCnt())
                    .isAnonymous(communityPosting.getIsAnonymous())
                    .isView(communityPosting.getIsView())
                    .communityVoteInfo(communityPosting.getCommunityVote() == null ? null : CommunityVoteResponseDto.CommunityVoteInfo.of(communityPosting.getCommunityVote()))
                    ;

            UserService userService = ApplicationContextUtil.getBean(UserService.class);
            User loginUser;
            if (userService.isLogin()) {
                loginUser = userService.getLoginUser();
            } else {
                loginUser = null;
            }

            if (loginUser != null && communityPosting.getWriter() != null && communityPosting.getWriter().getId().equals(loginUser.getId())) {
                builder.writer(UserResponseDto.UserSimple.of(communityPosting.getWriter()));
            } else {
                builder.writer(UserResponseDto.UserSimple.of(communityPosting.getIsAnonymous() ? null : communityPosting.getWriter()));
            }

            if (!communityPosting.getSubImages().isEmpty()) {
                builder.mainImageUrl(
                        ImagePathLengthConverter.extendImagePathLength(
                                communityPosting.getSubImages().stream()
                                        .filter(recruitmentImage -> recruitmentImage.getImageOrder() == 1)
                                        .findFirst()
                                        .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL))
                                        .getImage().getImageUrl()
                        )
                );
            } else {
                builder.mainImageUrl(StringUtils.EMPTY);
            }

            return builder.build();
        }

        public static List<CommunityPostSimple> of(List<CommunityPosting> communityPostings) {
            return communityPostings.stream().map(CommunityPostSimple::of).collect(Collectors.toList());
        }
    }
}
