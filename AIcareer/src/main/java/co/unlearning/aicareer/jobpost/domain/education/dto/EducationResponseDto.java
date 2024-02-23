package co.unlearning.aicareer.jobpost.domain.education.dto;

import co.unlearning.aicareer.jobpost.domain.education.Education;
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
    public static class EducationDto {
        @Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR"})
        private String education;
        public static EducationDto of(Education education) {
            return EducationDto.builder()
                    .education(String.valueOf(education.getDegree()))
                    .build();
        }
        public static List<EducationDto> of(List<Education> educations) {
            return educations.stream().map(EducationDto::of).collect(Collectors.toList());
        }
    }
}
