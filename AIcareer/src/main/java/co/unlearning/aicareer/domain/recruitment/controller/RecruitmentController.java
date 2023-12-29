package co.unlearning.aicareer.domain.recruitment.controller;

import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.UserErrorCode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
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
    @PostMapping("/")
    public ResponseEntity<RecruitmentResponseDto.RecruitmentInfo> postRecruitmentInfo(@RequestBody RecruitmentRequirementDto.RecruitmentPost recruitmentPost) {
        return ResponseEntity.ok(RecruitmentResponseDto.RecruitmentInfo.of(recruitmentService.addRecruitmentPost(recruitmentPost)));
    }
    @GetMapping("/test")
    @ApiResponse(
            responseCode = "200",
            description = "이미 회원가입을 했던 유저인 경우",
            content = @Content(
                    schema = @Schema(implementation = RecruitmentResponseDto.RecruitmentInfo.class)))
    @ApiErrorCodeExample(UserErrorCode.class)
    public void getUserErrorCode() {}
}
