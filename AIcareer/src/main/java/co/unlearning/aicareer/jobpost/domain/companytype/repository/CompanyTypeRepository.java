package co.unlearning.aicareer.jobpost.domain.companytype.repository;

import co.unlearning.aicareer.jobpost.domain.companytype.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyTypeRepository extends JpaRepository<CompanyType,Integer> {
}

