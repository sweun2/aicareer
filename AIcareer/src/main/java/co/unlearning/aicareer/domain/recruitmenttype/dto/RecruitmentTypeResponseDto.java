package co.unlearning.aicareer.domain.recruitmenttype.dto;

import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class RecruitmentTypeResponseDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitmentTypeNames {
        @Schema(description = "모집 직무 이름")
        private String recruitingTypeName;
        public static RecruitmentTypeNames of(RecruitmentType recruitmentType) {
            return RecruitmentTypeNames.builder()
                    .recruitingTypeName(String.valueOf(recruitmentType.getRecruitmentTypeName()))
                    .build();
        }
        public static List<RecruitmentTypeNames> of(List<RecruitmentType> recruitmentTypes) {
            return recruitmentTypes.stream().map(RecruitmentTypeNames::of).collect(Collectors.toList());
        }
    }
}
