package co.unlearning.aicareer.domain.community;

import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.job.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.job.recruitment.service.RecruitmentService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.GptService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.service.RecruitmentBatchService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Slf4j
@Tag(name = "recruitment batch", description = "스케줄링 api")
@RequiredArgsConstructor
@RequestMapping("/api/community/batch")

public class CommunityBatchController {
    private final CommunityBatchService communityBatchService;

    @GetMapping("/cleanFalseUser")
    public void cleanFalseUser(){
        communityBatchService.deleteAllOptionFalsePostingUserAndCommentUserNotWriter();
    }
}

