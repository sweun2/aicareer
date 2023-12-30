package co.unlearning.aicareer.domain.recruitment.controller;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.UserErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
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


    @Operation(summary = "글쓰기", description = "채용 공고 글쓰기")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Info.class)))
    @PostMapping("/")
    public ResponseEntity<RecruitmentResponseDto.Info> postRecruitmentInfo(@RequestBody RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        return ResponseEntity.ok(RecruitmentResponseDto.Info.of(recruitmentService.addRecruitmentPost(recruitmentPost)));
    }
    @Operation(summary = "글 조회", description = "글 조회, 무관/전체 필터링 시 해당 값을 안 보내야 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.Info.class)))
    @PostMapping("/search")
    public ResponseEntity<RecruitmentResponseDto.Simple> findRecruitmentInfo(@RequestBody RecruitmentRequirementDto.Search search,
                                                                             @Parameter(name = "page", description = "페이지네이션", in = ParameterIn.QUERY)
                                                                             @RequestParam("page") Integer page) throws Exception {
        PageRequest pageRequest = PageRequest.of(page,6);
        return ResponseEntity.ok(RecruitmentResponseDto.Simple.of(new Recruitment()));
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
