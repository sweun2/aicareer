package co.unlearning.aicareer.domain.companytype.repository;

import co.unlearning.aicareer.domain.companytype.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyTypeRepository extends JpaRepository<CompanyType,Integer> {
}

