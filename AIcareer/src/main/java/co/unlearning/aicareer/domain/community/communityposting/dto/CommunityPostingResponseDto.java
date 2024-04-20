package co.unlearning.aicareer.domain.community.communityposting.dto;

import co.unlearning.aicareer.domain.common.user.dto.UserResponseDto;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.util.List;
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
        private UserResponseDto.UserSimple userSimple;

        public static CommunityPostInfo of(CommunityPosting communityPosting) {
            return CommunityPostInfo.builder()
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
                    .userSimple(UserResponseDto.UserSimple.of(communityPosting.getWriter()))
                    .build();
        }

        public static List<CommunityPostInfo> of(List<CommunityPosting> communityPostings) {
            return communityPostings.stream().map(CommunityPostInfo::of).collect(Collectors.toList());
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
            return CommunityPostSimple.builder()
                    .uid(communityPosting.getUid())
                    .uploadDate(LocalDateTimeStringConverter.LocalDateTimeToString(communityPosting.getUploadDate()))
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(communityPosting.getLastModified()))
                    .title(communityPosting.getTitle())
                    .simpleContent(simpleContent)
                    .hits(communityPosting.getHits())
                    .commentCnt(communityPosting.getCommentCnt())
                    .reportCnt(communityPosting.getReportCnt())
                    .recommendCnt(communityPosting.getRecommendCnt())
                    .isView(communityPosting.getIsView())
                    .build();
        }

        public static List<CommunityPostSimple> of(List<CommunityPosting> communityPostings) {
            return communityPostings.stream().map(CommunityPostSimple::of).collect(Collectors.toList());
        }
    }
}
