package co.unlearning.aicareer.domain.recruitment.controller;

import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.user.service.UserService;
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
@Tag(name = "recruitment", description = "체용 공고 api")
@RequiredArgsConstructor
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    private final UserService userService;
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글쓰기", description = "채용 공고 글쓰기")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.DATE_BAD_REQUEST),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_ENUM_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED)

    })
    @PostMapping("/post")
    public ResponseEntity<RecruitmentResponseDto.RecruitmentInfo> postRecruitmentInfo(@RequestBody RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        userService.checkAdmin();
        return ResponseEntity.status(HttpStatus.CREATED).body(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.addRecruitmentPost(recruitmentPost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글 수정하기", description = "채용 공고 글 수정하기")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.DATE_BAD_REQUEST),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_ENUM_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED)

    })
    @PutMapping("/{uid}")
    public ResponseEntity<RecruitmentResponseDto.RecruitmentInfo> putRecruitmentInfo(@RequestBody RecruitmentRequirementDto.RecruitmentPost recruitmentPost,
                                                                                        @Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                                        @PathVariable("uid") String uid) throws Exception {
        userService.checkAdmin();
        return ResponseEntity.status(HttpStatus.OK).body(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.updateRecruitmentPost(uid,recruitmentPost)));
    }
    @Operation(summary = "여러 공고 조회", description = "필터링 글 조회, 무관/전체 필터링 시 해당 값을 안 보내야 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentSimple.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_ENUM_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_DATE_STRING_INPUT),
            @ApiErrorCodeExample(ResponseErrorCode.DATE_BAD_REQUEST),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST),
    })
    @PostMapping("/search")
    public ResponseEntity<List<RecruitmentResponseDto.RecruitmentSimple>> findAllRecruitmentInfo(
            @RequestBody RecruitmentRequirementDto.Search search,
            @Parameter(name = "page", description = "페이지네이션", in = ParameterIn.QUERY)
            @RequestParam("page") Integer page) {
            log.info("search con");
            PageRequest pageRequest = PageRequest.of(page, 6);
            return ResponseEntity.ok(RecruitmentResponseDto.RecruitmentSimple.of(recruitmentService.getFilteredRecruitment(search, pageRequest)));
    }
    @Operation(summary = "단일 글 조회", description = "단일 글 조회, 공고 uid 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentInfo.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
    })
    @GetMapping("/{uid}")
    public ResponseEntity<RecruitmentResponseDto.RecruitmentInfo> findRecruitmentInfo(
            @Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) {
        return ResponseEntity.ok(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.getOneRecruitmentPostWithUpdateHits(uid)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글 삭제", description = "글 삭제, 공고 uid 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @DeleteMapping("/delete/{uid}")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED)

    })
    public ResponseEntity<Void> removeRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                          @PathVariable("uid") String uid)  {
        userService.checkAdmin();
        recruitmentService.deleteRecruitmentByUid(uid);
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "채용 공고 북마크 등록", description = "채용 공고 북마크하기, 로그인 필요")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentSimple.class)))
    @PostMapping("/bookmark/post/{uid}")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    public ResponseEntity<RecruitmentResponseDto.RecruitmentSimple> postBookmarkRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                                     @PathVariable("uid") String uid) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(RecruitmentResponseDto.RecruitmentSimple.of(recruitmentService.addRecruitmentBookmark(uid)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "채용 공고 북마크된 목록 보기", description = "채용 공고 북마크보기, 로그인 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentSimple.class))
            ))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @GetMapping("/bookmark/")
    public ResponseEntity<List<RecruitmentResponseDto.RecruitmentSimple>> findBookmarkRecruitmentInfo() throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(RecruitmentResponseDto.RecruitmentSimple.of(recruitmentService.findUserBookMark()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "채용 공고 북마크 제거", description = "채용 공고 북마크 취소하기, 로그인 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_FOUND),
            @ApiErrorCodeExample(ResponseErrorCode.USER_UNAUTHORIZED),
    })
    @DeleteMapping("/bookmark/delete/{uid}")
    public ResponseEntity<Void> removeBookmarkRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                                         @PathVariable("uid") String uid) throws Exception {
        recruitmentService.removeRecruitmentBookMark(uid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
