package co.unlearning.aicareer.domain.community.communityposting.dto;

import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

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
        @Schema(description = "글쓴이 정보")
        private UserResponseDto.UserSimple writer;
        @Schema(description = "로그인 유저의 정보")
        private CommunityPostingUserResponseDto.CommunityPostingUserInfo communityPostingUserInfo;

        public static CommunityPostInfo of(Map.Entry<CommunityPosting,CommunityPostingUser> postingUserEntry) {
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
                    .communityPostingUserInfo(CommunityPostingUserResponseDto.CommunityPostingUserInfo.of(communityPostingUser))
                    .writer(UserResponseDto.UserSimple.of(communityPosting.getWriter()))
                    ;
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
        public static CommunityPostSimple of(CommunityPosting communityPosting) {
            String simpleContent = communityPosting.getContent();
            if(communityPosting.getContent().length()>20) {
                simpleContent = simpleContent.substring(0,20);
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
                    .isView(communityPosting.getIsView());

            if(!communityPosting.getSubImages().isEmpty()) {
                builder.mainImageUrl(
                        ImagePathLengthConverter.extendImagePathLength(
                                communityPosting.getSubImages().stream()
                                        .filter(recruitmentImage->recruitmentImage.getImageOrder() == 1).toList().get(0)
                                        .getImage().getImageUrl())
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
