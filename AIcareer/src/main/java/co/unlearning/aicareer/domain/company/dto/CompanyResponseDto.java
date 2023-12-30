package co.unlearning.aicareer.domain.company.dto;

import co.unlearning.aicareer.domain.CompanyType.dto.CompanyTypeResponseDto;
import co.unlearning.aicareer.domain.careerrequirement.dto.CareerRequirementResponseDto;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyResponseDto {
    @Builder
    @Getter
    @Setter
    public static class CompanyInfo {
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE"})
        private List<CompanyTypeResponseDto.CompanyTypeInfo> companyTypeInfos;
        @Schema(description = "회사 주소")
        private String companyAddress;

        public static CompanyInfo of(Company company) {
            return CompanyInfo.builder()
                    .companyName(company.getCompanyName())
                    .companyTypeInfos(CompanyTypeResponseDto.CompanyTypeInfo.of(List.copyOf(company.getCompanyTypeSet())))
                    .companyAddress(company.getCompanyAddress())
                    .build();
        }
        public static List<CompanyInfo> of(List<Company> companies) {
            return companies.stream().map(CompanyInfo::of).collect(Collectors.toList());
        }
    }
}
