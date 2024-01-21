package co.unlearning.aicareer.domain.recruitment.service;

import co.unlearning.aicareer.domain.Image.service.ImageService;
import co.unlearning.aicareer.domain.bookmark.Bookmark;
import co.unlearning.aicareer.domain.bookmark.repository.BookmarkRepository;
import co.unlearning.aicareer.domain.companyType.CompanyType;
import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.career.Career;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.company.service.CompanyService;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.RecruitmentAddress;
import co.unlearning.aicareer.domain.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentSpecification;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.user.User;
import co.unlearning.aicareer.domain.user.repository.UserRepository;
import co.unlearning.aicareer.domain.user.service.UserService;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import co.unlearning.aicareer.global.utils.validator.TimeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageService imageService;
    public Recruitment getOneRecruitmentPostWithUpdateHits(String uid) {
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        recruitment.setHits(recruitment.getHits()+1);

        return recruitment;
    }

    public Recruitment findRecruitmentInfoByUid(String uid) {
        return recruitmentRepository.findByUid(uid).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public Recruitment updateRecruitmentPost(String uid, RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        Image image = imageService.getImageByUrl(ImagePathLengthConverter.slicingImagePathLength(recruitmentPost.getMainImage()));
        Company company = companyService.addNewCompany(
                CompanyRequirementDto.CompanyInfo.builder()
                        .companyAddress(recruitmentPost.getCompanyAddress())
                        .companyName(recruitmentPost.getCompanyName())
                        .companyType(recruitmentPost.getCompanyType())
                .build());
        EnumValidator<RecruitmentDeadlineType> recruitmentDeadlineTypeEnumValidator = new EnumValidator<>();
        RecruitmentDeadlineType recruitmentDeadlineType = recruitmentDeadlineTypeEnumValidator.validateEnumString(recruitmentPost.getRecruitmentDeadline().getDeadlineType(), RecruitmentDeadlineType.class);
        EnumValidator<RecruitmentAddress> recruitmentAddressEnumValidator = new EnumValidator<>();
        RecruitmentAddress recruitmentAddress = recruitmentAddressEnumValidator.validateEnumString(recruitmentPost.getRecruitmentAddress(),RecruitmentAddress.class);
        recruitment.setMainImage(image);
        recruitment.setCompany(company);

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

        recruitment.setRecruitmentStartDate(LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentStartDate()));
        recruitment.setRecruitmentDeadlineType(recruitmentDeadlineType);
        recruitment.setRecruitmentDeadline(LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentDeadline().getRecruitmentDeadline()));
        recruitment.setRecruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink());
        recruitment.setRecruitmentAddress(recruitmentAddress);
        recruitment.setTitle(recruitmentPost.getTitle());
        recruitment.setContent(recruitmentPost.getContent());
        recruitment.setLastModified(LocalDateTime.now());
        // Save the updated Recruitment entity
        return recruitmentRepository.save(recruitment);
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
        Image image = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(recruitmentPost.getMainImage())).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
        );

        //모집 공고 위치
        EnumValidator<RecruitmentAddress> recruitmentAddressEnumValidator = new EnumValidator<>();
        RecruitmentAddress recruitmentAddress = recruitmentAddressEnumValidator.validateEnumString(recruitmentPost.getRecruitmentAddress(),RecruitmentAddress.class);

        Recruitment recruitment = Recruitment.builder()
                .uid(UUID.randomUUID().toString())
                .company(companyTemp)
                .recruitmentStartDate(startDate)
                .recruitmentDeadlineType(deadlineType)
                .recruitmentDeadline(deadLine)
                .uploadDate(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .recruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink()) //validator 필요
                .mainImage(image)
                .content(recruitmentPost.getContent())
                .recruitmentAddress(recruitmentAddress)
                .title(recruitmentPost.getTitle())
                .hits(0)
                .build();

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
        return recruitmentRepository.save(recruitment);
    }

    public List<Recruitment> getFilteredRecruitment(RecruitmentRequirementDto.Search search, Pageable pageable) {
        log.info("search");
        List<RecruitingJob.RecruitingJobName> recruitingJobList = new ArrayList<>();
        List<CompanyType.CompanyTypeName> companyTypeNameList = new ArrayList<>();
        List<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameList = new ArrayList<>();
        List<Education.DEGREE> dgreeList = new ArrayList<>();
        List<Career.AnnualLeave> annualLeaveList = new ArrayList<>();
        List<RecruitmentAddress> recruitmentAddresses = new ArrayList<>();

        if (!search.getRecruitingJobNames().isEmpty()) {
            EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobEnumValidator = new EnumValidator<>();
            for (String recruitJobNameStr : search.getRecruitingJobNames()) {
                 RecruitingJob.RecruitingJobName recruitingJobName = recruitingJobEnumValidator.validateEnumString(recruitJobNameStr, RecruitingJob.RecruitingJobName.class);
                 recruitingJobList.add(recruitingJobName);
            }
        } else recruitingJobList.addAll(List.of(RecruitingJob.RecruitingJobName.values()));
        if(!search.getCompanyTypes().isEmpty()) {
            EnumValidator<CompanyType.CompanyTypeName> companyTypeNameEnumValidator = new EnumValidator<>();
            for(String companyTypeStr : search.getCompanyTypes()) {
                CompanyType.CompanyTypeName companyTypeName =  companyTypeNameEnumValidator.validateEnumString(companyTypeStr, CompanyType.CompanyTypeName.class);
                companyTypeNameList.add(companyTypeName);
            }
        } else companyTypeNameList.addAll(List.of(CompanyType.CompanyTypeName.values()));
        if (!search.getRecruitmentTypeNames().isEmpty()) {
            EnumValidator<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameEnumValidator = new EnumValidator<>();
            for (String recruitmentTypeStr : search.getRecruitmentTypeNames()) {
                RecruitmentType.RecruitmentTypeName recruitmentTypeName = recruitmentTypeNameEnumValidator.validateEnumString(recruitmentTypeStr, RecruitmentType.RecruitmentTypeName.class);
                recruitmentTypeNameList.add(recruitmentTypeName);
            }
        } else recruitmentTypeNameList.addAll(List.of(RecruitmentType.RecruitmentTypeName.values()));
        if (!search.getEducations().isEmpty()) {
            EnumValidator<Education.DEGREE> educationNameEnumValidator = new EnumValidator<>();
            for (String educationNameStr : search.getEducations()) {
                Education.DEGREE degree = educationNameEnumValidator.validateEnumString(educationNameStr, Education.DEGREE.class);
                dgreeList.add(degree);
            }
        } else dgreeList.addAll(List.of(Education.DEGREE.values()));
        if (!search.getCareers().isEmpty()) {
            EnumValidator<Career.AnnualLeave> careerNameEnumValidator = new EnumValidator<>();
            for (String careerNameStr : search.getCareers()) {
                Career.AnnualLeave annualLeave = careerNameEnumValidator.validateEnumString(careerNameStr, Career.AnnualLeave.class);
                annualLeaveList.add(annualLeave);
            }
        } else annualLeaveList.addAll(List.of(Career.AnnualLeave.values()));
        if(!search.getRecruitmentAddress().isEmpty()) {
            EnumValidator<RecruitmentAddress> recruitmentAddressEnumValidator = new EnumValidator<>();
            for (String recruitmentAddressStr : search.getRecruitmentAddress()) {
                RecruitmentAddress recruitmentAddress = recruitmentAddressEnumValidator.validateEnumString(recruitmentAddressStr, RecruitmentAddress.class);
                recruitmentAddresses.add(recruitmentAddress);
            }
        } else recruitmentAddresses.addAll(List.of(RecruitmentAddress.values()));
        log.info("search");

        //마감된 공고 처리 true 면 아직 마감 안된 공고
        if(!search.getIsOpen()) {
            Specification<Recruitment> specification = Specification.where(RecruitmentSpecification.hasRecruitingJob(recruitingJobList))
                    .and(RecruitmentSpecification.hasCompanyType(companyTypeNameList))
                    .and(RecruitmentSpecification.hasRecruitmentType(recruitmentTypeNameList))
                    .and(RecruitmentSpecification.hasEducation(dgreeList))
                    .and(RecruitmentSpecification.hasCareer(annualLeaveList))
                    .and(RecruitmentSpecification.hasRecruitmentAddress(recruitmentAddresses))
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
                        ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
                )
        );
    }
    public Recruitment addRecruitmentBookmark(String uid) {
        User user = userService.getLoginUser();
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .recruitment(recruitment)
                .build();
        Set<Bookmark> userBookmarkSet = user.getBookmarkSet();
        userBookmarkSet.add(bookmark);
        user.setBookmarkSet(userBookmarkSet);

        Set<Bookmark> recruitBookmarkSet = recruitment.getBookmarkSet();
        recruitBookmarkSet.add(bookmark);
        recruitment.setBookmarkSet(recruitBookmarkSet);

        recruitment.setBookmarkSet(recruitBookmarkSet);

        bookmarkRepository.save(bookmark);
        return recruitment;
    }
    public void removeRecruitmentBookMark(String uid) {
        User user = userService.getLoginUser();
        Recruitment recruitment = findRecruitmentInfoByUid(uid);
        Bookmark bookmark = bookmarkRepository.findByUserAndRecruitment(user,recruitment).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR)
        );
        bookmarkRepository.delete(bookmark);
        userRepository.save(user);
    }
    public List<Recruitment> findUserBookMark() {
        User user = userService.getLoginUser();
        List<Bookmark> bookmarkList = bookmarkRepository.findAllByUser(user);
        List<Recruitment> recruitmentList = new ArrayList<>();
        bookmarkList.forEach(
                bookmark -> recruitmentList.add(bookmark.getRecruitment())
        );
        return recruitmentList;
    }
}