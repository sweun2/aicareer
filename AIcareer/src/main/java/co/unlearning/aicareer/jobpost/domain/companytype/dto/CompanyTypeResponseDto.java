package co.unlearning.aicareer.jobpost.domain.companytype.dto;

import co.unlearning.aicareer.jobpost.domain.companytype.CompanyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyTypeResponseDto {
    @Builder
    @Getter
    @Setter
    public static class CompanyTypeInfo {
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC", "ETC"})
        private String companyTypeName;

        public static CompanyTypeInfo of(CompanyType companyType) {
            return CompanyTypeInfo.builder()
                    .companyTypeName(String.valueOf(companyType.getCompanyTypeName()))
                    .build();
        }
        public static List<CompanyTypeInfo> of(List<CompanyType> companyTypes) {
            return companyTypes.stream().map(CompanyTypeInfo::of).collect(Collectors.toList());
        }
    }
}
