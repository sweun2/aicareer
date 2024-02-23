package co.unlearning.aicareer.jobpost.domain.career.dto;

import co.unlearning.aicareer.jobpost.domain.career.Career;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CareerResponseDto {
    @Getter
    @Setter
    @Builder
    public static class CareerDto {
        @Schema(description = "경력 조건",allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER","IRRELEVANCE"})
        private String career;
        public static CareerDto of(Career career) {
            return CareerDto.builder()
                    .career(String.valueOf(career.getAnnualLeave()))
                    .build();
        }
        public static List<CareerDto> of(List<Career> careers) {
            return careers.stream().map(CareerDto::of).collect(Collectors.toList());
        }
    }
}
