package co.unlearning.aicareer.domain.job.recruitmenttype.dto;

import co.unlearning.aicareer.domain.job.recruitmenttype.RecruitmentType;
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
    public static class RecruitmentTypeNameDto {
        @Schema(description = "모집 직무 이름",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private String recruitingTypeName;
        public static RecruitmentTypeNameDto of(RecruitmentType recruitmentType) {
            return RecruitmentTypeNameDto.builder()
                    .recruitingTypeName(String.valueOf(recruitmentType.getRecruitmentTypeName()))
                    .build();
        }
        public static List<RecruitmentTypeNameDto> of(List<RecruitmentType> recruitmentTypes) {
            return recruitmentTypes.stream().map(RecruitmentTypeNameDto::of).collect(Collectors.toList());
        }
    }
}
