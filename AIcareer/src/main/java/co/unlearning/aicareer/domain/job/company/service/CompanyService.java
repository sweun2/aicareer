package co.unlearning.aicareer.domain.job.company.service;

import co.unlearning.aicareer.domain.job.company.Company;
import co.unlearning.aicareer.domain.job.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.job.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompanyService {
    private final CompanyRepository companyRepository;
    public Company getCompanyByCompanyName(String companyName) {
        return companyRepository.findByCompanyName(companyName).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"company name not found")
        );
    }
    public Company addNewCompany(CompanyRequirementDto.CompanyInfo companyInfo) throws Exception {
        Optional<Company> companyOptional = companyRepository.findByCompanyName(companyInfo.getCompanyName());
        if(companyOptional.isEmpty()) {
            EnumValidator<CompanyType.CompanyTypeName> companyTypeNameEnumValidator = new EnumValidator<>();
            CompanyType.CompanyTypeName companyTypeName = companyTypeNameEnumValidator.validateEnumString(companyInfo.getCompanyType(), CompanyType.CompanyTypeName.class);

            CompanyType companyType = CompanyType.builder()
                    .companyTypeName(companyTypeName)
                    .build();

            Company company = Company.builder()
                    .companyName(companyInfo.getCompanyName())
                    .companyAddress(companyInfo.getCompanyAddress())
                    .companyType(companyType)
                    .uid(UUID.randomUUID().toString())
                    .build();

            return companyRepository.save(company);
        }else return companyOptional.get();
    }
}
