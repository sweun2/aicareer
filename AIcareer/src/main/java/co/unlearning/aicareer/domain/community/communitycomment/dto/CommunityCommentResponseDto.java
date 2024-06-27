package co.unlearning.aicareer.domain.community.communitycomment.dto;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitycommentuser.dto.CommunityCommentUserResponseDto;
import co.unlearning.aicareer.domain.community.communitycommentuser.service.CommunityCommentUserService;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.global.utils.ApplicationContextUtil;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
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
        @Schema(description = "익명 여부")
        private Boolean isAnonymous;
        @Schema(description = "글쓴이 정보")
        private UserResponseDto.UserSimple writer;
        @Schema(description = "상위 댓글 uid")
        private String parentCommentUid;
        @Schema(description = "하위 댓글 정보")
        private List<CommunityCommentInfo> childComments;
        @Schema(description = "댓글 유저 정보")
        private CommunityCommentUserResponseDto.CommunityCommentUserInfo communityCommentUserInfo;

        public static CommunityCommentInfo of(Map.Entry<CommunityComment, CommunityCommentUser> commentUserEntry) {
            UserService userService = ApplicationContextUtil.getBean(UserService.class);
            CommunityCommentUserService communityCommentUserService = ApplicationContextUtil.getBean(CommunityCommentUserService.class);
            User loginUser;
            if(userService.isLogin()) {
                loginUser = userService.getLoginUser(); // 로그인 유저 정보 가져오기
            } else {
                loginUser = null;
            }

            CommunityComment communityComment = commentUserEntry.getKey();
            CommunityCommentUser communityCommentUser = commentUserEntry.getValue();

            UserResponseDto.UserSimple writerInfo;
            if (loginUser!=null && communityComment.getWriter().getId().equals(loginUser.getId())) {
                writerInfo = UserResponseDto.UserSimple.of(communityComment.getWriter());
            } else {
                writerInfo = UserResponseDto.UserSimple.of(communityComment.getIsAnonymous() ? null : communityComment.getWriter());
            }

            List<CommunityCommentInfo> sortedChildComments = null;
            if (communityComment.getChildComments() != null) {
                sortedChildComments = communityComment.getChildComments().stream()
                        .sorted(Comparator.comparing(CommunityComment::getUploadDate))
                        .limit(3)
                        .map(childComment -> {
                            CommunityCommentUser childCommentUser = (loginUser != null)
                                    ? communityCommentUserService.getCommunityCommentUserByUserAndCommunityComment(loginUser, childComment)
                                    : communityCommentUserService.getMockCommunityCommentUserIfNotLogin(childComment);
                            return of(Map.entry(childComment, childCommentUser));
                        })
                        .collect(Collectors.toList());
            }

            return CommunityCommentInfo.builder()
                    .uid(communityComment.getUid())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityComment.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(communityComment.getLastModified()))
                    .content(communityComment.getContent())
                    .reportCnt(communityComment.getReportCnt())
                    .recommendCnt(communityComment.getRecommendCnt())
                    .isView(communityComment.getIsView())
                    .isAnonymous(communityComment.getIsAnonymous())
                    .writer(writerInfo)
                    .parentCommentUid(communityComment.getParentComment() == null ? StringUtils.EMPTY : communityComment.getParentComment().getUid())
                    .childComments(sortedChildComments)
                    .communityCommentUserInfo(CommunityCommentUserResponseDto.CommunityCommentUserInfo.of(communityCommentUser))
                    .build();
        }

        public static List<CommunityCommentInfo> of(List<Map.Entry<CommunityComment, CommunityCommentUser>> commentUserEntry) {
            return commentUserEntry.stream().map(CommunityCommentInfo::of).collect(Collectors.toList());
        }
    }
}
