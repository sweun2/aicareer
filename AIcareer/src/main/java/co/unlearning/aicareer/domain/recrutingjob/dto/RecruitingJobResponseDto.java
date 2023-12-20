package co.unlearning.aicareer.domain.recrutingjob.dto;

import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
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
    public static class RecruitingJobNames {
        @Schema(description = "모집 직무 이름")
        private String recruitingJobName;
        public static RecruitingJobNames of(RecruitingJob recruitingJob) {
            return RecruitingJobNames.builder()
                    .recruitingJobName(recruitingJob.getRecruitJobName())
                    .build();
        }
        public static List<RecruitingJobNames> of(List<RecruitingJob> recruitingJob) {
            return recruitingJob.stream().map(RecruitingJobNames::of).collect(Collectors.toList());
        }
    }
}
