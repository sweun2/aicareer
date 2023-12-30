package co.unlearning.aicareer.domain.company.service;

import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.recruitment.CompanyType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
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
            return companyRepository.save(
                    Company.builder()
                            .companyName(companyInfo.getCompanyName())
                            .companyType(CompanyType.ofCompanyType(companyInfo.getCompanyType()))
                            .companyAddress(companyInfo.getCompanyAddress())
                            .uid(Long.valueOf(UUID.randomUUID().toString()))
                            .build()
            );
        }else return companyOptional.get();
    }
}
