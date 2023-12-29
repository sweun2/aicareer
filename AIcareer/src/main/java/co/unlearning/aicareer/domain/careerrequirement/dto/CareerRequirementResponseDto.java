package co.unlearning.aicareer.domain.careerrequirement.dto;

import co.unlearning.aicareer.domain.careerrequirement.CareerRequirement;
import co.unlearning.aicareer.domain.education.Education;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CareerRequirementResponseDto {
    @Getter
    @Setter
    @Builder
    public static class Career {
        @Schema(description = "요구 경력")
        private String careerRequirement;
        public static Career of(CareerRequirement careerRequirement) {
            return Career.builder()
                    .careerRequirement(careerRequirement.getRequirement())
                    .build();
        }
        public static List<Career> of(List<CareerRequirement> careerRequirements) {
            return careerRequirements.stream().map(Career::of).collect(Collectors.toList());
        }
    }
}
