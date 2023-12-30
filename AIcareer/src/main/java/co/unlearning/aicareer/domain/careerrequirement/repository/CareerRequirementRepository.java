package co.unlearning.aicareer.domain.careerrequirement.repository;

import co.unlearning.aicareer.domain.careerrequirement.CareerRequirement;
import co.unlearning.aicareer.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerRequirementRepository extends JpaRepository<CareerRequirement,Integer> {
}

