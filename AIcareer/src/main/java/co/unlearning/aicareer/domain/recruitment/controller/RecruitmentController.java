package co.unlearning.aicareer.domain.recruitment.controller;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.code.UserErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "recruitment", description = "체용 공고 api")
@RequiredArgsConstructor
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
/*    @GetMapping("/{uid}")
    public ResponseEntity<RecruitmentResponseDto.RecruitmentInfo> getRecruitmentInfo(@PathVariable String uid) {
        return ResponseEntity.ok(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.findCompanyInfo(uid)));
    }
    @GetMapping("/all")
    public ResponseEntity<List<RecruitmentResponseDto.RecruitmentInfo>> getAllRecruitment() {
        return ResponseEntity.ok(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.findAllCompanyInfo()));
    }*/

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글쓰기", description = "채용 공고 글쓰기")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Info.class)))
    @PostMapping("/")
    public ResponseEntity<RecruitmentResponseDto.Info> postRecruitmentInfo(@RequestBody RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(RecruitmentResponseDto.Info.of(recruitmentService.addRecruitmentPost(recruitmentPost)));
    }

    @Operation(summary = "여러 공고 조회", description = "필터링 글 조회, 무관/전체 필터링 시 해당 값을 안 보내야 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Simple.class)))
    @PostMapping("/search")
    public ResponseEntity<List<RecruitmentResponseDto.Simple>> findAllRecruitmentInfo(@RequestBody RecruitmentRequirementDto.Search search,
                                                                             @Parameter(name = "page", description = "페이지네이션", in = ParameterIn.QUERY)
                                                                             @RequestParam("page") Integer page) throws Exception {
        PageRequest pageRequest = PageRequest.of(page,6);
        return ResponseEntity.ok(RecruitmentResponseDto.Simple.of(new ArrayList<>()) );
    }
    @Operation(summary = "단일 글 조회", description = "단일 글 조회, 공고 uid 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Info.class)))
    @GetMapping("/{uid}")
    public ResponseEntity<RecruitmentResponseDto.Info> findRecruitmentInfo(
            @Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
            @PathVariable("uid") String uid) throws Exception {
        return ResponseEntity.ok().build();
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "글 삭제", description = "글 삭제, 공고 uid 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> removeRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                             @PathVariable("uid") String uid) throws Exception {
        return ResponseEntity.ok().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "채용 공고 북마크 등록", description = "채용 공고 북마크하기, 로그인 필요")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Simple.class)))
    @PostMapping("/bookmark/{uid}")
    public ResponseEntity<RecruitmentResponseDto.Simple> postBookmarkRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                                     @PathVariable("uid") String uid) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(RecruitmentResponseDto.Simple.of(new Recruitment()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "채용 공고 북마크된 목록 보기", description = "채용 공고 북마크보기, 로그인 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Simple.class)))
    @GetMapping("/bookmark/")
    public ResponseEntity<RecruitmentResponseDto.Simple> findBookmarkRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                                     @PathVariable("uid") String uid) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(RecruitmentResponseDto.Simple.of(new Recruitment()));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "채용 공고 북마크 제거", description = "채용 공고 북마크 취소하기, 로그인 필요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답")
    @DeleteMapping("/bookmark/{uid}")
    public ResponseEntity<Void> removeBookmarkRecruitmentInfo(@Parameter(name = "uid", description = "공고 uid", in = ParameterIn.PATH)
                                                                                 @PathVariable("uid") String uid) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).build();
    }



    @GetMapping("/test")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Info.class)))
    @ApiErrorCodeExample(UserErrorCode.class)
    public void getUserErrorCode() {}
}
