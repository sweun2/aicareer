package co.unlearning.aicareer.domain.recruitment.service;

import co.unlearning.aicareer.domain.career.Career;
import co.unlearning.aicareer.domain.career.dto.CareerResponseDto;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.company.service.CompanyService;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.repository.RecruitingJobRepository;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final RecruitingJobRepository recruitingJobRepository;
    public Recruitment findRecruitmentInfo(String uid) {
        return recruitmentRepository.findByUid(Long.valueOf(uid)).orElseThrow(
                () -> new ResponseStatusException(HttpStatusCode.valueOf(404),"text")
        );
    }
    /*public Recruitment findAllRecruitmentInfo() {
        return recruitmentRepository.findByUid().orElseThrow(
                () -> new ResponseStatusException(HttpStatusCode.valueOf(404),"text")
        );
    }*/
    public Recruitment addRecruitmentPost(RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        //image 처리 필요

        //company 등록 안된 경우 예외 처리
        Optional<Company> companyOptional = companyRepository.findByCompanyName(recruitmentPost.getCompanyName());
        Company companyTemp;
        if(companyOptional.isEmpty()) {
            companyTemp = companyService.addNewCompany(CompanyRequirementDto.CompanyInfo.builder()
                            .companyName(recruitmentPost.getCompanyName())
                            .companyAddress(recruitmentPost.getCompanyAddress())
                            .companyType(recruitmentPost.getCompanyType())
                    .build());
        }else {
            companyTemp = companyOptional.get();
        }
        Set<RecruitingJob> recruitingJobs = new HashSet<>();
        for(RecruitingJobResponseDto.RecruitingJobNameDto recruitingJobNameDto : recruitmentPost.getRecruitingJobNames()) {
            EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobNameEnumValidator = new EnumValidator<>();
            recruitingJobs.add(RecruitingJob.builder()
                    .recruitJobName(recruitingJobNameEnumValidator.validateEnumString(recruitingJobNameDto.getRecruitingJobName(),RecruitingJob.RecruitingJobName.class))
                    .build());
        }
        Set<RecruitmentType> recruitmentTypes = new HashSet<>();
        for(RecruitmentTypeResponseDto.RecruitmentTypeNameDto recruitmentTypeNameDto : recruitmentPost.getRecruitmentTypeNames()) {
            EnumValidator<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameValidator = new EnumValidator<>();
            recruitmentTypes.add(RecruitmentType.builder()
                    .recruitmentTypeName(recruitmentTypeNameValidator.validateEnumString(recruitmentTypeNameDto.getRecruitingTypeName(), RecruitmentType.RecruitmentTypeName.class))
                    .build());
        }
        Set<Education> educations = new HashSet<>();
        for(EducationResponseDto.EducationDto educationDto : recruitmentPost.getEducationDtos()) {
            EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
            educations.add(Education.builder()
                            .degree(degreeEnumValidator.validateEnumString(educationDto.getEducation(), Education.DEGREE.class))
                    .build());
        }
        Set<Career> careers = new HashSet<>();
        for(CareerResponseDto.CareerDto careerDto : recruitmentPost.getCareerDtoRequirements()) {
            EnumValidator<Career.AnnualLeave> annualLeaveEnumValidator = new EnumValidator<>();
            careers.add(Career.builder()
                            .annualLeave(annualLeaveEnumValidator.validateEnumString(careerDto.getCareer(), Career.AnnualLeave.class))
                    .build());
        }
        //모집 시작일
        LocalDateTime startDate;
        if(recruitmentPost.getRecruitmentStartDate()==null) {
            startDate = LocalDateTime.now();
        } else {
            startDate = recruitmentPost.getRecruitmentStartDate();
        }
        //모집 마감일
        LocalDateTime deadLine;
        if(recruitmentPost.getRecruitmentDeadline()==null) {
            deadLine = LocalDateTime.now();
        } else {
            deadLine = recruitmentPost.getRecruitmentDeadline();
        }
        /*
        private String recruitmentAnnouncementLink; //모집 공고 링크
        @Schema(description = "조회수",allowableValues = {})
        private Integer hits; //조회수*/
        Recruitment recruitment = Recruitment.builder()
                .company(companyTemp)
                .recruitingJobSet(recruitingJobs)
                .recruitmentTypeSet(recruitmentTypes)
                .educationSet(educations)
                .careerSet(careers)
                .recruitmentStartDate(startDate)
                .recruitmentDeadline(deadLine)
                .uploadDate(LocalDateTime.now())
                .recruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink()) //validator 필요
                .hits(0)
                .build();

        return recruitmentRepository.save(recruitment);
    }
}
