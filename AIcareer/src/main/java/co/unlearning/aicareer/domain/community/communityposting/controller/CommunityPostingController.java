package co.unlearning.aicareer.domain.community.communityposting.controller;

import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingRequirementDto;
import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingResponseDto;
import co.unlearning.aicareer.domain.community.communityposting.service.CommunityPostingService;
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

@Slf4j
@Tag(name = "community posting", description = "커뮤니티 api")
@RestController
@RequestMapping("/api/community/posting")
@RequiredArgsConstructor
public class CommunityPostingController {
    private final CommunityPostingService communityPostingService;
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 글쓰기", description = "커뮤니티 글쓰기")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.USER_BLOCKED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @PostMapping("/post")
    public ResponseEntity<CommunityPostingResponseDto.CommunityPostInfo> postCommunityPosting(@RequestBody CommunityPostingRequirementDto.CommunityPostingPost communityPostingPost) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityPostingResponseDto.CommunityPostInfo.of(communityPostingService.addCommunityPost(communityPostingPost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 글수정", description = "커뮤니티 글수정")
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
    public ResponseEntity<CommunityPostingResponseDto.CommunityPostInfo> updateCommunityPost(
            @Parameter(name = "uid", description = "게시글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid,
            @RequestBody CommunityPostingRequirementDto.CommunityPostingPost communityPostingPost) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityPostingResponseDto.CommunityPostInfo.of(communityPostingService.updateCommunityPost(uid,communityPostingPost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 글삭제", description = "커뮤니티 글삭제")
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
            @Parameter(name = "uid", description = "게시글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) throws Exception {
        communityPostingService.deleteCommunityPostByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 글 한개 반환", description = "커뮤니티의 글 리스트를 반환합니다. isView가 true인 일반 유저가 볼 수 있는객체들만 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)

    })
    @GetMapping("/{uid}")
    public ResponseEntity<CommunityPostingResponseDto.CommunityPostInfo> getCommunityPosting(
            @Parameter(name = "uid", description = "게시글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid){
        communityPostingService.updatePostingHits(uid);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityPostingResponseDto.CommunityPostInfo.of(communityPostingService.getCommunityPostingByUid(uid)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 글 리스트 반환", description = "커뮤니티의 글 리스트를 반환합니다. isView가 true인 일반 유저가 볼 수 있는객체들만 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostSimple.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
    })
    @GetMapping("/list")
    public ResponseEntity<List<CommunityPostingResponseDto.CommunityPostSimple>> getAllCommunityPosting(
            @Parameter(name = "page", description = "페이지네이션", in = ParameterIn.QUERY)
            @RequestParam("page") Integer page){
        PageRequest pageRequest = PageRequest.of(page, 10);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityPostingResponseDto.CommunityPostSimple.of(communityPostingService.getAllCommunityPostingIsViewTrue(pageRequest)));
    }
    @Operation(summary = "제목/내용 검색", description = "커뮤니티 글 제목/내용 검색")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingResponseDto.CommunityPostSimple.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
    })
    @PostMapping("/search/{keyword}")
    public ResponseEntity<List<CommunityPostingResponseDto.CommunityPostSimple>> searchAllRecruitmentSimple(
            @Parameter(name = "keyword", description = "검색어", in = ParameterIn.PATH)
            @PathVariable("keyword") String keyword,
            @Parameter(name = "page", description = "페이지네이션", in = ParameterIn.QUERY)
            @RequestParam("page") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        return ResponseEntity.ok(CommunityPostingResponseDto.CommunityPostSimple.of(communityPostingService.getAllCommunityPostingSearchByKeyword(keyword,pageRequest)));
    }
    @Operation(summary = "게시글 추천", description = "커뮤니티 게시글 추천")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingUserResponseDto.CommunityPostingUserInfo.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)
    })
    @PostMapping("/recommend/{uid}")
    public ResponseEntity<CommunityPostingUserResponseDto.CommunityPostingUserInfo> recommendCommunityPosting(
            @Parameter(name = "uid", description = "게시글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid,
            @Parameter(name = "status", description = "추천 여부, true or false", in = ParameterIn.QUERY)
            @RequestParam("status") Boolean status) {
        return ResponseEntity.ok(CommunityPostingUserResponseDto.CommunityPostingUserInfo.of(communityPostingService.recommendCommunityPosting(uid,status)));
    }
    @Operation(summary = "게시글 신고", description = "커뮤니티 게시글 신고")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingUserResponseDto.CommunityPostingUserInfo.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND)
    })
    @PostMapping("/report/{uid}")
    public ResponseEntity<CommunityPostingUserResponseDto.CommunityPostingUserInfo> reportCommunityPosting(
            @Parameter(name = "uid", description = "게시글 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid,
            @Parameter(name = "status", description = "신고 여부, true or false", in = ParameterIn.QUERY)
            @RequestParam("status") Boolean status) {
        return ResponseEntity.ok(CommunityPostingUserResponseDto.CommunityPostingUserInfo.of(communityPostingService.reportCommunityPosting(uid,status)));
    }
    @Operation(summary = "인기글 반환", description = "금일 조회수 + 좋아요 수 + 댓글 수 가 제일 높은 3개의 posting 반환")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CommunityPostingUserResponseDto.CommunityPostingUserInfo.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
    })
    @GetMapping("/hot")
    public ResponseEntity<List<CommunityPostingResponseDto.CommunityPostSimple>> searchAllRecruitmentSimple() {
        return ResponseEntity.ok(CommunityPostingResponseDto.CommunityPostSimple.of(communityPostingService.getTopPostsForToday()));
    }
}
