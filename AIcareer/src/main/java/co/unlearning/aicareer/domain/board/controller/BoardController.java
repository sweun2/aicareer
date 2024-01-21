package co.unlearning.aicareer.domain.board.controller;

import co.unlearning.aicareer.domain.board.dto.BoardRequirementDto;
import co.unlearning.aicareer.domain.board.dto.BoardResponseDto;
import co.unlearning.aicareer.domain.board.service.BoardService;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
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
@Tag(name = "board", description = "공지/배너 api")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글쓰기", description = "배너에 들어갈 글 글쓰기")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = BoardResponseDto.BoardInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
    })
    @PostMapping("/post")
    public ResponseEntity<BoardResponseDto.BoardInfo> postBoardInfo(@RequestBody BoardRequirementDto.BoardPost boardPost) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(BoardResponseDto.BoardInfo.of(boardService.addBoardPost(boardPost)));
    }
    @Operation(summary = "보드 글 리스트 반환", description = "공지 글 전부 반환")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = BoardResponseDto.BoardSimple.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR)
    })
    @PostMapping("/search")
    public ResponseEntity<List<BoardResponseDto.BoardSimple>> findAllBoardInfo() {
        return ResponseEntity.ok(BoardResponseDto.BoardSimple.of(boardService.getBoardList()));
    }
    @Operation(summary = "단일 글 조회", description = "단일 보드 글 조회, uid 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = BoardResponseDto.BoardInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
    })
    @GetMapping("/{uid}")
    public ResponseEntity<BoardResponseDto.BoardInfo> findBoardInfo(
            @Parameter(name = "uid", description = "보드 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) {
        return ResponseEntity.ok(BoardResponseDto.BoardInfo.of(boardService.getBoardByUid(uid)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글 삭제", description = "글 삭제, 보드 uid 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @DeleteMapping("/delete/{uid}")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
    })
    public ResponseEntity<Void> removeBoardInfo(@Parameter(name = "uid", description = "보드 uid", in = ParameterIn.PATH)
                                                      @PathVariable("uid") String uid)  {
        boardService.removeBoardByUid(uid);
        return ResponseEntity.ok().build();
    }
}
