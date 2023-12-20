package co.unlearning.aicareer.domain.recruitment.controller;

import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
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
}
