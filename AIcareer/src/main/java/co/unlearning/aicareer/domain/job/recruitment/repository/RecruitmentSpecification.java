package co.unlearning.aicareer.domain.job.recruitment.repository;

import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.career.Career;
import co.unlearning.aicareer.domain.job.company.Company;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentAddress;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class RecruitmentSpecification {
    public static Specification<Recruitment> hasRecruitingJob(List<RecruitingJob.RecruitingJobName> recruitingJobNames) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<Recruitment, RecruitingJob> recruitingJobJoin = root.join("recruitingJobSet", JoinType.INNER);

            // Use join to access the attribute in RecruitingJob entity
            Path<RecruitingJob.RecruitingJobName> recruitingJobNamePath = recruitingJobJoin.get("recruitJobName");

            return recruitingJobNamePath.in(recruitingJobNames);
        };
    }
    public static Specification<Recruitment> hasCompanyType(List<CompanyType.CompanyTypeName> companyTypeNames) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<Recruitment, Company> companyJoin = root.join("company", JoinType.INNER);
            Join<Company,CompanyType> companyTypeJoin = companyJoin.join("companyType");
            // Use join to access the attribute in RecruitingJob entity
            Path<CompanyType.CompanyTypeName> companyTypeNamePath = companyTypeJoin.get("companyTypeName");

            return companyTypeNamePath.in(companyTypeNames);
        };
    }
    public static Specification<Recruitment> hasRecruitmentType(List<RecruitmentType.RecruitmentTypeName> recruitmentTypeNames) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<Recruitment, RecruitmentType> recruitmentTypeJoin = root.join("recruitmentTypeSet", JoinType.INNER);

            // Use join to access the attribute in RecruitingJob entity
            Path<RecruitmentType.RecruitmentTypeName> recruitmentTypeNamePath = recruitmentTypeJoin.get("recruitmentTypeName");

            return recruitmentTypeNamePath.in(recruitmentTypeNames);
        };
    }
    public static Specification<Recruitment> hasEducation(List<Education.DEGREE> degrees) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<Recruitment, Education> educationJoin = root.join("educationSet", JoinType.INNER);

            // Use join to access the attribute in RecruitingJob entity
            Path<Education.DEGREE> degreePath = educationJoin.get("degree");

            return degreePath.in(degrees);
        };
    }
    public static Specification<Recruitment> hasCareer(List<Career.AnnualLeave> annualLeaves) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            Join<Recruitment, Career> careerJoin = root.join("careerSet", JoinType.INNER);

            // Use join to access the attribute in RecruitingJob entity
            Path<Career.AnnualLeave> annualLeavePath = careerJoin.get("annualLeave");

            return annualLeavePath.in(annualLeaves);
        };
    }
    public static Specification<Recruitment> hasDeadlineType(List<RecruitmentDeadlineType> deadlineTypes) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and(root.get("recruitmentDeadlineType").in(deadlineTypes));
        };
    }

    public static Specification<Recruitment> hasRecruitmentAddress(List<RecruitmentAddress> recruitmentAddresses) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and(root.get("recruitmentAddress").in(recruitmentAddresses));
        };
    }
    public static Specification<Recruitment> isOpenRecruitment() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("recruitmentDeadline"), LocalDateTime.now());
    }
    public static Specification<Recruitment> notInDeadlineTypes(List<RecruitmentDeadlineType> deadlineTypes) {
        return (root, query, builder) -> builder.not(root.get("recruitmentDeadlineType").in(deadlineTypes));
    }
    public static Specification<Recruitment> uploadDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("uploadDate"), start, end);
    }
}
