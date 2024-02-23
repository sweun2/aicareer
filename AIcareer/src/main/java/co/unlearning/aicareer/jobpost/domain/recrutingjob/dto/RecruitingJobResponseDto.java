package co.unlearning.aicareer.jobpost.domain.recrutingjob.dto;

import co.unlearning.aicareer.jobpost.domain.recrutingjob.RecruitingJob;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class RecruitingJobResponseDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitingJobNameDto {
        @Schema(description = "모집 직무 이름",allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC"})
        private String recruitingJobName;
        public static RecruitingJobNameDto of(RecruitingJob recruitingJob) {
            return RecruitingJobNameDto.builder()
                    .recruitingJobName(String.valueOf(recruitingJob.getRecruitJobName()))
                    .build();
        }
        public static List<RecruitingJobNameDto> of(List<RecruitingJob> recruitingJob) {
            return recruitingJob.stream().map(RecruitingJobNameDto::of).collect(Collectors.toList());
        }
    }
}
