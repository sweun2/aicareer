package co.unlearning.aicareer.domain.community.communitycomment.dto;

import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitycommentuser.dto.CommunityCommentUserResponseDto;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommunityCommentResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityCommentInfo {
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "업로드 시간")
        private String uploadDate;
        @Schema(description = "최종 수정일")
        private String lastModified; // 최종 변경일
        @Schema(description = "내용")
        private String content; //내용
        @Schema(description = "추천 수")
        private Integer reportCnt; //내용
        @Schema(description = "신고 수")
        private Integer recommendCnt; //내용
        @Schema(description = "볼 수 있는지 여부/ 신고 횟수 초과시 가려짐")
        private Boolean isView;
        @Schema(description = "글쓴이 정보")
        private UserResponseDto.UserSimple writer;
        private CommunityCommentUserResponseDto.CommunityCommentUserInfo communityCommentUserInfo;


        public static CommunityCommentInfo of(Map.Entry<CommunityComment, CommunityCommentUser> commentUserEntry) {
            CommunityComment communityComment = commentUserEntry.getKey();
            CommunityCommentUser communityCommentUser = commentUserEntry.getValue();

            return CommunityCommentInfo.builder()
                    .uid(communityComment.getUid())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityComment.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(communityComment.getLastModified()))
                    .content(communityComment.getContent())
                    .reportCnt(communityComment.getReportCnt())
                    .recommendCnt(communityComment.getRecommendCnt())
                    .isView(communityComment.getIsView())
                    .writer(UserResponseDto.UserSimple.of(communityComment.getIsAnonymous() ? null : communityCommentUser.getUser()))
                    .communityCommentUserInfo(CommunityCommentUserResponseDto.CommunityCommentUserInfo.of(communityCommentUser))
                    .build();
        }

        public static List<CommunityCommentInfo> of(List<Map.Entry<CommunityComment, CommunityCommentUser>> commentUserEntry) {
            return commentUserEntry.stream().map(CommunityCommentInfo::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityCommentSimple {
        @Schema(description = "글 uid")
        private String uid;
        @Schema(description = "업로드 시간")
        private String uploadDate;
        @Schema(description = "최종 수정일")
        private String lastModified; // 최종 변경일
        @Schema(description = "내용")
        private String content; //내용
        @Schema(description = "추천 수")
        private Integer reportCnt; //내용
        @Schema(description = "신고 수")
        private Integer recommendCnt; //내용
        @Schema(description = "볼 수 있는지 여부/ 신고 횟수 초과시 가려짐")
        private Boolean isView;
        public static CommunityCommentSimple of(CommunityComment communityComment) {
            return CommunityCommentSimple.builder()
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityComment.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(communityComment.getLastModified()))
                    .content(communityComment.getContent())
                    .reportCnt(communityComment.getReportCnt())
                    .recommendCnt(communityComment.getRecommendCnt())
                    .isView(communityComment.getIsView())
                    .build();
        }

        public static List<CommunityCommentSimple> of(List<CommunityComment> communityComments) {
            return communityComments.stream().map(CommunityCommentSimple::of).collect(Collectors.toList());
        }
    }
}
