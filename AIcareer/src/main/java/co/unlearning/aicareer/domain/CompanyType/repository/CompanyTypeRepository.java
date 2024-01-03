package co.unlearning.aicareer.domain.CompanyType.repository;

import co.unlearning.aicareer.domain.CompanyType.CompanyType;
import co.unlearning.aicareer.domain.company.Company;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyTypeRepository extends JpaRepository<CompanyType,Integer> {
}

