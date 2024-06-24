package co.unlearning.aicareer.domain.community.communityvote.controller;

import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingRequirementDto;
import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingResponseDto;
import co.unlearning.aicareer.domain.community.communityposting.service.CommunityPostingService;
import co.unlearning.aicareer.domain.community.communitypostinguser.dto.CommunityPostingUserResponseDto;
import co.unlearning.aicareer.domain.community.communitypostinguser.service.CommunityPostingUserService;
import co.unlearning.aicareer.domain.community.communityvote.dto.CommunityVoteRequestDto;
import co.unlearning.aicareer.domain.community.communityvote.dto.CommunityVoteResponseDto;
import co.unlearning.aicareer.domain.community.communityvote.service.CommunityVoteService;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "community posting vote", description = "커뮤니티 투표 api")
@RestController
@RequestMapping("/api/community/posting/vote")
@RequiredArgsConstructor
public class CommunityVoteController {
    private final CommunityVoteService communityVoteService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 투표 생성", description = "커뮤니티 글에 투표 생성하기")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityVoteResponseDto.CommunityVoteInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.USER_BLOCKED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @PostMapping("/")
    public ResponseEntity<CommunityVoteResponseDto.CommunityVoteInfo> createCommunityVote(
            @RequestBody CommunityVoteRequestDto.VotePost votePost) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityVoteResponseDto.CommunityVoteInfo.of(communityVoteService.createVote(votePost)));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "커뮤니티 투표 선택", description = "커뮤니티 투표하기, 투표가 존재하지 않을 시 에러")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = CommunityVoteResponseDto.CommunityVoteInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.USER_BLOCKED),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @PostMapping("/vote")
    public ResponseEntity<CommunityVoteResponseDto.CommunityVoteInfo> castVote(
            @RequestBody CommunityVoteRequestDto.CastVoteOption castVoteOption) {
        return ResponseEntity.ok().body(CommunityVoteResponseDto.CommunityVoteInfo.of(communityVoteService.castVote(castVoteOption)));
    }


}
