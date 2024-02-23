package co.unlearning.aicareer.domain.job.company.repository;

import co.unlearning.aicareer.domain.job.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Integer> {
    Optional<Company> findByCompanyName(String name);
    Optional<Company> findByUid(String uid);
}

