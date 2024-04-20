package co.unlearning.aicareer.domain.community.communitycomment.controller;

import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycomment.dto.CommunityCommentRequirementDto;
import co.unlearning.aicareer.domain.community.communitycomment.dto.CommunityCommentResponseDto;
import co.unlearning.aicareer.domain.community.communitycomment.service.CommunityCommentService;
import co.unlearning.aicareer.domain.community.communitycommentuser.dto.CommunityCommentUserResponseDto;
import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingResponseDto;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@Slf4j
@Tag(name = "community comment", description = "커뮤니티 댓글 api")
@RequiredArgsConstructor
@RequestMapping("/api/community/comment")
public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 글에 대한 댓글 반환", description = "커뮤니티의 글의 댓글 리스트를 반환합니다. isView가 true인 일반 유저가 볼 수 있는객체들만 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityCommentResponseDto.CommunityCommentInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)
    })
    @GetMapping("/{uid}")
    public ResponseEntity<List<CommunityCommentResponseDto.CommunityCommentInfo>> filterAllRecruitmentSimple(
            @Parameter(name = "uid", description = "게시글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid,
            @Parameter(name = "page", description = "페이지네이션", in = ParameterIn.QUERY)
            @RequestParam("page") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 10);

        return ResponseEntity.ok(CommunityCommentResponseDto.CommunityCommentInfo.of(communityCommentService.getCommunityCommentsByCommunityPosting(uid,pageRequest)));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글쓰기", description = "커뮤니티 글에 댓글쓰기")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityCommentResponseDto.CommunityCommentInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.USER_BLOCKED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)
    })
    @PostMapping("/post")
    public ResponseEntity<CommunityCommentResponseDto.CommunityCommentInfo> postCommunityPosting(@RequestBody CommunityCommentRequirementDto.CommunityCommentPost communityCommentPost) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityCommentResponseDto.CommunityCommentInfo.of(communityCommentService.addCommunityComment(communityCommentPost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글 수정", description = "커뮤니티 댓글 수정")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_BLOCKED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @PutMapping("/update/{uid}")
    public ResponseEntity<CommunityCommentResponseDto.CommunityCommentInfo> updateCommunityComment(
            @Parameter(name = "uid", description = "댓글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid,
            @RequestBody CommunityCommentRequirementDto.CommunityCommentPost communityCommentPost) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityCommentResponseDto.CommunityCommentInfo.of(communityCommentService.updateCommunityPost(uid,communityCommentPost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "댓글삭제", description = "커뮤니티 댓글삭제")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_BLOCKED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<Void> deleteCommunityPost(
            @Parameter(name = "uid", description = "댓글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) throws Exception {
        communityCommentService.deleteCommunityCommentByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @Operation(summary = "댓글 추천", description = "커뮤니티 댓글 추천")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostSimple.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)
    })
    @PostMapping("/recommend/{uid}")
    public ResponseEntity<CommunityCommentUserResponseDto.CommunityCommentUserInfo> recommendCommunityComment(
            @Parameter(name = "uid", description = "댓글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) {
        return ResponseEntity.ok(CommunityCommentUserResponseDto.CommunityCommentUserInfo.of(communityCommentService.recommendCommunityComment(uid)));
    }
    @Operation(summary = "댓글 신고", description = "커뮤니티 댓글 신고")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostSimple.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)
    })
    @PostMapping("/report/{uid}")
    public ResponseEntity<CommunityCommentUserResponseDto.CommunityCommentUserInfo> reportCommunityComment(
            @Parameter(name = "uid", description = "댓글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) {
        return ResponseEntity.ok(CommunityCommentUserResponseDto.CommunityCommentUserInfo.of(communityCommentService.reportCommunityComment(uid)));
    }
}
