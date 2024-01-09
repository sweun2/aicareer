package co.unlearning.aicareer.domain.recruitment.service;

import co.unlearning.aicareer.domain.CompanyType.CompanyType;
import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.Image.service.ImageService;
import co.unlearning.aicareer.domain.career.Career;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.company.service.CompanyService;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentSpecification;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import co.unlearning.aicareer.global.utils.validator.TimeValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final UserRepository userRepository;
    public Recruitment getOneRecruitmentPostWithUpdateHits(String uid) {
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        recruitment.setHits(recruitment.getHits()+1);

        return recruitment;
    }

    public Recruitment findRecruitmentInfoByUid(String uid) {
        return recruitmentRepository.findByUid(uid).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.RECRUITMENT_UID_NOT_FOUND)
        );
    }
    public Recruitment updateRecruitmentPost(String uid, RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        // 수정 필요
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        Image image = recruitment.getMainImage();
        Image newImage = Image.builder()
                .createdDate(image.getCreatedDate())
                .imageUrl(image.getImageUrl())
                .absolutePath(image.getAbsolutePath())
                .build();

        deleteRecruitmentByUid(uid);

        imageRepository.save(newImage);

        return addRecruitmentPost(recruitmentPost);
    }
    public Recruitment addRecruitmentPost(RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        //company 등록 안된 경우 예외 처리
        Optional<Company> companyOptional = companyRepository.findByCompanyName(recruitmentPost.getCompanyName());
        Company companyTemp;
        if(companyOptional.isEmpty()) {
            companyTemp = companyService.addNewCompany(
                    CompanyRequirementDto.CompanyInfo.builder()
                            .companyName(recruitmentPost.getCompanyName())
                            .companyAddress(recruitmentPost.getCompanyAddress())
                            .companyType(recruitmentPost.getCompanyType())
                    .build());
        }else {
            companyTemp = companyOptional.get();
        }
        log.info("company");
        //모집 시작일
        LocalDateTime startDate;
        if(recruitmentPost.getRecruitmentStartDate()==null) {
            startDate = LocalDateTime.now();
        } else {
            startDate =  LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentStartDate());
        }
        //모집 마감일 ALL_TIME, CLOSE_WHEN_RECRUITMENT, DUE_DATE
        EnumValidator<RecruitmentDeadlineType> recruitmentDeadlineTypeEnumValidator = new EnumValidator<>();
        RecruitmentDeadlineType deadlineType = recruitmentDeadlineTypeEnumValidator.validateEnumString(recruitmentPost.getRecruitmentDeadline().getDeadlineType(),RecruitmentDeadlineType.class);
        LocalDateTime deadLine;
        if(deadlineType == RecruitmentDeadlineType.DUE_DATE) {
            deadLine = LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentDeadline().getRecruitmentDeadline());
            //마감 일이 미래 인지 확인
            TimeValidator.RemainingTimeValidator(deadLine);
        } else{
            deadLine = LocalDateTime.of(2999,12,12,12,12);
        }
        log.info("date");
        Image image = imageRepository.findByImageUrl(recruitmentPost.getMainImage()).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
        );

        Recruitment recruitment = Recruitment.builder()
                .uid(UUID.randomUUID().toString())
                .company(companyTemp)
                .recruitmentStartDate(startDate)
                .recruitmentDeadlineType(deadlineType)
                .recruitmentDeadline(deadLine)
                .uploadDate(LocalDateTime.now())
                .recruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink()) //validator 필요
                .mainImage(image)
                .content(recruitmentPost.getContent())
                .recruitmentAddress(recruitmentPost.getRecruitmentAddress())
                .hits(0)
                .build();
        log.info("recruitment");

        Set<RecruitingJob> recruitingJobs = new HashSet<>();
        for(String strRecruitingJobNameDto : recruitmentPost.getRecruitingJobNames()) {
            EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobNameEnumValidator = new EnumValidator<>();
            recruitingJobs.add(RecruitingJob.builder()
                    .recruitJobName(recruitingJobNameEnumValidator.validateEnumString(strRecruitingJobNameDto,RecruitingJob.RecruitingJobName.class))
                    .recruitment(recruitment)
                    .build());
        }
        Set<RecruitmentType> recruitmentTypes = new HashSet<>();
        for(String strRecruitmentTypeNameDto : recruitmentPost.getRecruitmentTypeNames()) {
            EnumValidator<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameValidator = new EnumValidator<>();
            recruitmentTypes.add(RecruitmentType.builder()
                    .recruitment(recruitment)
                    .recruitmentTypeName(recruitmentTypeNameValidator.validateEnumString(strRecruitmentTypeNameDto, RecruitmentType.RecruitmentTypeName.class))
                    .build());
        }
        Set<Education> educations = new HashSet<>();
        for(String strEducationDto : recruitmentPost.getEducations()) {
            EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
            educations.add(Education.builder()
                    .recruitment(recruitment)
                            .degree(degreeEnumValidator.validateEnumString(strEducationDto, Education.DEGREE.class))
                    .build());
        }
        Set<Career> careers = new HashSet<>();
        for(String strCareerDto : recruitmentPost.getCareers()) {
            EnumValidator<Career.AnnualLeave> annualLeaveEnumValidator = new EnumValidator<>();
            careers.add(Career.builder()
                    .recruitment(recruitment)
                            .annualLeave(annualLeaveEnumValidator.validateEnumString(strCareerDto, Career.AnnualLeave.class))
                    .build());
        }
        recruitment.setRecruitingJobSet(recruitingJobs);
        recruitment.setRecruitmentTypeSet(recruitmentTypes);
        recruitment.setEducationSet(educations);
        recruitment.setCareerSet(careers);
        log.info("oneToMany");
        return recruitmentRepository.save(recruitment);
    }

    public List<Recruitment> getFilteredRecruitment(RecruitmentRequirementDto.Search search, Pageable pageable) {
        List<RecruitingJob.RecruitingJobName> recruitingJobList = new ArrayList<>();
        List<CompanyType.CompanyTypeName> companyTypeNameList = new ArrayList<>();
        List<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameList = new ArrayList<>();
        List<Education.DEGREE> dgreeList = new ArrayList<>();
        List<Career.AnnualLeave> annualLeaveList = new ArrayList<>();

        if (!search.getRecruitingJobNames().isEmpty()) {
            EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobEnumValidator = new EnumValidator<>();
            for (String recruitJobNameStr : search.getRecruitingJobNames()) {
                 RecruitingJob.RecruitingJobName recruitingJobName = recruitingJobEnumValidator.validateEnumString(recruitJobNameStr, RecruitingJob.RecruitingJobName.class);
                 recruitingJobList.add(recruitingJobName);
            }
        }
        if(!search.getCompanyTypes().isEmpty()) {
            EnumValidator<CompanyType.CompanyTypeName> companyTypeNameEnumValidator = new EnumValidator<>();
            for(String companyTypeStr : search.getCompanyTypes()) {
                CompanyType.CompanyTypeName companyTypeName =  companyTypeNameEnumValidator.validateEnumString(companyTypeStr, CompanyType.CompanyTypeName.class);
                companyTypeNameList.add(companyTypeName);
            }
        }
        if (!search.getRecruitmentTypeNames().isEmpty()) {
            EnumValidator<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameEnumValidator = new EnumValidator<>();
            for (String recruitmentTypeStr : search.getRecruitmentTypeNames()) {
                RecruitmentType.RecruitmentTypeName recruitmentTypeName = recruitmentTypeNameEnumValidator.validateEnumString(recruitmentTypeStr, RecruitmentType.RecruitmentTypeName.class);
                recruitmentTypeNameList.add(recruitmentTypeName);
            }
        }
        if (!search.getEducations().isEmpty()) {
            EnumValidator<Education.DEGREE> educationNameEnumValidator = new EnumValidator<>();
            for (String educationNameStr : search.getEducations()) {
                Education.DEGREE degree = educationNameEnumValidator.validateEnumString(educationNameStr, Education.DEGREE.class);
                dgreeList.add(degree);
            }
        }
        if (!search.getCareers().isEmpty()) {
            EnumValidator<Career.AnnualLeave> careerNameEnumValidator = new EnumValidator<>();
            for (String careerNameStr : search.getCareers()) {
                Career.AnnualLeave annualLeave = careerNameEnumValidator.validateEnumString(careerNameStr, Career.AnnualLeave.class);
                annualLeaveList.add(annualLeave);
            }
        }
        //마감된 공고 처리 true 면 아직 마감 안된 공고
        if(!search.getIsOpen()) {
            Specification<Recruitment> specification = Specification.where(RecruitmentSpecification.hasRecruitingJob(recruitingJobList))
                    .and(RecruitmentSpecification.hasCompanyType(companyTypeNameList))
                    .and(RecruitmentSpecification.hasRecruitmentType(recruitmentTypeNameList))
                    .and(RecruitmentSpecification.hasEducation(dgreeList))
                    .and(RecruitmentSpecification.hasCareer(annualLeaveList))
                    .and(RecruitmentSpecification.hasRecruitmentAddress(search.getRecruitmentAddress()))
                    .and(RecruitmentSpecification.isOpenRecruitment())
                    ;
            return getOrder(search, pageable, specification);
        }else {
            Specification<Recruitment> specification = Specification.where(RecruitmentSpecification.hasRecruitingJob(recruitingJobList))
                    .and(RecruitmentSpecification.hasCompanyType(companyTypeNameList))
                    .and(RecruitmentSpecification.hasRecruitmentType(recruitmentTypeNameList))
                    .and(RecruitmentSpecification.hasEducation(dgreeList))
                    .and(RecruitmentSpecification.hasCareer(annualLeaveList))
                    ;
            return getOrder(search, pageable, specification);
        }
    }
    private List<Recruitment> getOrder(RecruitmentRequirementDto.Search search, Pageable pageable, Specification<Recruitment> specification) {
        Sort sort;
        String sortAttribute = switch (search.getSortCondition()) {
            case "HITS" -> "hits";
            case "DEADLINE" -> "recruitmentDeadline"; // or use the correct attribute name
            case "UPLOAD" -> "uploadDate";
            default -> throw new BusinessException(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST);
        };

        if (Objects.equals(search.getOrderBy(), "DESC")) {
            sort = Sort.by(sortAttribute).descending();
        } else if (Objects.equals(search.getOrderBy(), "ASC")) {
            sort = Sort.by(Sort.Direction.ASC, sortAttribute);
        } else {
            throw new BusinessException(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST);
        }

        PageRequest pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return recruitmentRepository.findAll(specification, pageableWithSort).stream().toList();
    }
    public void deleteRecruitmentByUid (String uid) {
        recruitmentRepository.delete(
                recruitmentRepository.findByUid(uid).orElseThrow(
                        ()-> new BusinessException(ResponseErrorCode.RECRUITMENT_UID_NOT_FOUND)
                )
        );
    }
    public Recruitment addRecruitmentBookmark(String uid) {
        User user = userService.getLoginUser();
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        user.getBookMark().add(recruitment);

        userRepository.save(user);
        return recruitment;
    }
    public void removeRecruitmentBookMark(String uid) {
        User user = userService.getLoginUser();
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        user.getBookMark().remove(recruitment);

        userRepository.save(user);
    }
    public List<Recruitment> findUserBookMark() {
        User user = userService.getLoginUser();
        return user.getBookMark().stream().toList();
    }

}