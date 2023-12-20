package co.unlearning.aicareer.domain.education.dto;

import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class EducationResponseDto {
    @Getter
    @Setter
    @Builder
    public static class EducationRequirement {
        @Schema(description = "학력 조건")
        private String educationRequirement;
        public static EducationRequirement of(Education education) {
            return EducationRequirement.builder()
                    .educationRequirement(String.valueOf(education.getDegree()))
                    .build();
        }
        public static List<EducationRequirement> of(List<Education> educations) {
            return educations.stream().map(EducationRequirement::of).collect(Collectors.toList());
        }
    }
}
