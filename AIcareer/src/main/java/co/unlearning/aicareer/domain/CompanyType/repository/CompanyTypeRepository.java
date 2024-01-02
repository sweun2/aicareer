package co.unlearning.aicareer.domain.CompanyType.repository;

import co.unlearning.aicareer.domain.CompanyType.CompanyType;
import co.unlearning.aicareer.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyTypeRepository extends JpaRepository<CompanyType,Integer> {
}

