package co.unlearning.aicareer.jobpost.domain.company.dto;

import co.unlearning.aicareer.jobpost.domain.companytype.dto.CompanyTypeResponseDto;
import co.unlearning.aicareer.jobpost.domain.company.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyResponseDto {
    @Builder
    @Getter
    @Setter
    public static class CompanyResponseInfo {
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC", "ETC"})
        private CompanyTypeResponseDto.CompanyTypeInfo companyTypeInfo;
        @Schema(description = "회사 주소")
        private String companyAddress;

        public static CompanyResponseInfo of(Company company) {
            return CompanyResponseInfo.builder()
                    .companyName(company.getCompanyName())
                    .companyTypeInfo(CompanyTypeResponseDto.CompanyTypeInfo.of(company.getCompanyType()))
                    .companyAddress(company.getCompanyAddress())
                    .build();
        }
        public static List<CompanyResponseInfo> of(List<Company> companies) {
            return companies.stream().map(CompanyResponseInfo::of).collect(Collectors.toList());
        }
    }
}
