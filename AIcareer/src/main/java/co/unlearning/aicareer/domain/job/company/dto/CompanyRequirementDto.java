package co.unlearning.aicareer.domain.job.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class CompanyRequirementDto {
    @Getter
    @Setter
    @Builder
    public static class CompanyInfo {
        @Schema(description = "회사 주소")
        private String companyAddress;
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET"})
        private String companyType;
    }
}
