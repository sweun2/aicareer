package co.unlearning.aicareer.domain.company.service;

import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.companyType.CompanyType;
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
            CompanyType companyType = CompanyType.builder()
                    .companyTypeName(CompanyType.CompanyTypeName.valueOf(companyInfo.getCompanyType()))
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
