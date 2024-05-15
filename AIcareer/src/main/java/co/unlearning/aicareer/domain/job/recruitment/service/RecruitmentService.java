package co.unlearning.aicareer.domain.job.recruitment.service;

import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.bookmark.Bookmark;
import co.unlearning.aicareer.domain.job.bookmark.repository.BookmarkRepository;
import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.job.career.Career;
import co.unlearning.aicareer.domain.job.company.Company;
import co.unlearning.aicareer.domain.job.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.job.company.dto.CompanyRequirementDto;
import co.unlearning.aicareer.domain.job.company.service.CompanyService;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentAddress;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentSpecification;
import co.unlearning.aicareer.domain.job.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.job.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.common.sitemap.service.SiteMapService;
import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.repository.UserRepository;
import co.unlearning.aicareer.domain.common.user.service.UserService;
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
    private final SiteMapService siteMapService;
    public Recruitment getOneRecruitmentPostWithUpdateHits(String uid) {
        Recruitment recruitment = findRecruitmentByUid(uid);
        recruitment.setHits(recruitment.getHits()+1);

        return recruitment;
    }

    public Recruitment findRecruitmentByUid(String uid) {
        return recruitmentRepository.findByUid(uid).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public Recruitment updateRecruitmentPost(String uid, RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        Recruitment recruitment = findRecruitmentByUid(uid);
        Company company = companyService.addNewCompany(
                CompanyRequirementDto.CompanyInfo.builder()
                        .companyAddress(recruitmentPost.getCompanyAddress())
                        .companyName(recruitmentPost.getCompanyName())
                        .companyType(recruitmentPost.getCompanyType())
                        .build());
        EnumValidator<RecruitmentDeadlineType> recruitmentDeadlineTypeEnumValidator = new EnumValidator<>();
        RecruitmentDeadlineType recruitmentDeadlineType = recruitmentDeadlineTypeEnumValidator.validateEnumString(recruitmentPost.getRecruitmentDeadline().getDeadlineType(), RecruitmentDeadlineType.class);
        EnumValidator<RecruitmentAddress> recruitmentAddressEnumValidator = new EnumValidator<>();
        RecruitmentAddress recruitmentAddress = recruitmentAddressEnumValidator.validateEnumString(recruitmentPost.getRecruitmentAddress(), RecruitmentAddress.class);

        Image mainImage = null;
        if(recruitmentPost.getMainImage()!= null) {
            mainImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(recruitmentPost.getMainImage())).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }
        assert mainImage != null;
        mainImage.setIsRelated(true);

        recruitment.setMainImage(mainImage);
        recruitment.setCompany(company);

        Set<Image> subImages = new HashSet<>();
        for (String subImageUrl : recruitmentPost.getSubImage()) {
            Image subImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                    () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
            subImage.setIsRelated(true);
            subImages.add(subImage);
        }

        Set<RecruitingJob> recruitingJobs = new HashSet<>();
        for (String strRecruitingJobNameDto : recruitmentPost.getRecruitingJobNames()) {
            EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobNameEnumValidator = new EnumValidator<>();
            recruitingJobs.add(RecruitingJob.builder()
                    .recruitJobName(recruitingJobNameEnumValidator.validateEnumString(strRecruitingJobNameDto, RecruitingJob.RecruitingJobName.class))
                    .recruitment(recruitment)
                    .build());
        }
        Set<RecruitmentType> recruitmentTypes = new HashSet<>();
        for (String strRecruitmentTypeNameDto : recruitmentPost.getRecruitmentTypeNames()) {
            EnumValidator<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameValidator = new EnumValidator<>();
            recruitmentTypes.add(RecruitmentType.builder()
                    .recruitment(recruitment)
                    .recruitmentTypeName(recruitmentTypeNameValidator.validateEnumString(strRecruitmentTypeNameDto, RecruitmentType.RecruitmentTypeName.class))
                    .build());
        }
        Set<Education> educations = new HashSet<>();
        for (String strEducationDto : recruitmentPost.getEducations()) {
            EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
            educations.add(Education.builder()
                    .recruitment(recruitment)
                    .degree(degreeEnumValidator.validateEnumString(strEducationDto, Education.DEGREE.class))
                    .build());
        }
        Set<Career> careers = new HashSet<>();
        for (String strCareerDto : recruitmentPost.getCareers()) {
            EnumValidator<Career.AnnualLeave> annualLeaveEnumValidator = new EnumValidator<>();
            careers.add(Career.builder()
                    .recruitment(recruitment)
                    .annualLeave(annualLeaveEnumValidator.validateEnumString(strCareerDto, Career.AnnualLeave.class))
                    .build());
        }
        recruitment.getRecruitingJobSet().clear();
        recruitment.getRecruitingJobSet().addAll(recruitingJobs);

        recruitment.getRecruitmentTypeSet().clear();
        recruitment.getRecruitmentTypeSet().addAll(recruitmentTypes);

        recruitment.getEducationSet().clear();
        recruitment.getEducationSet().addAll(educations);

        recruitment.getCareerSet().clear();
        recruitment.getCareerSet().addAll(careers);

        recruitment.getSubImageSet().clear();
        recruitment.getSubImageSet().addAll(subImages);

        recruitment.setRecruitmentStartDate(LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentStartDate()));
        recruitment.setRecruitmentDeadlineType(recruitmentDeadlineType);

        if(recruitmentDeadlineType.equals(RecruitmentDeadlineType.DUE_DATE)) {
            recruitment.setRecruitmentDeadline(LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentDeadline().getRecruitmentDeadline()));
        } else if (recruitmentDeadlineType.equals(RecruitmentDeadlineType.EXPIRED)) {
            recruitment.setRecruitmentDeadline(LocalDateTime.of(2000,1,1,0,0));
        }
        else {
            recruitment.setRecruitmentDeadline((LocalDateTime.of(2999,12,12,12,12)));
        }
        recruitment.setRecruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink());
        recruitment.setRecruitmentAddress(recruitmentAddress);
        recruitment.setTitle(recruitmentPost.getTitle());
        recruitment.setContent(recruitmentPost.getContent());
        recruitment.setLastModified(LocalDateTime.now());
        //recruitment.setRecruitmentContentType(recruitmentPost.getContentType());

        recruitmentRepository.save(recruitment);

        siteMapService.registerRecruitmentSiteMap(recruitment);
        log.info("test");
        return recruitment;
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
        Image mainImage = null;
        if(recruitmentPost.getMainImage()!= null) {
            mainImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(recruitmentPost.getMainImage())).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
        }
        assert mainImage != null;
        mainImage.setIsRelated(true);

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
                .mainImage(mainImage)
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
        if(recruitmentPost.getEducations().isEmpty()) {
            educations.add(Education.builder()
                    .recruitment(recruitment)
                    .degree(Education.DEGREE.IRRELEVANCE)
                    .build());
        }else {
            for(String strEducationDto : recruitmentPost.getEducations()) {
                EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
                educations.add(Education.builder()
                        .recruitment(recruitment)
                        .degree(degreeEnumValidator.validateEnumString(strEducationDto, Education.DEGREE.class))
                        .build());
            }
        }

        Set<Career> careers = new HashSet<>();
        if(recruitmentPost.getCareers().isEmpty()) {
            careers.add(Career.builder()
                    .recruitment(recruitment)
                    .annualLeave(Career.AnnualLeave.IRRELEVANCE)
                    .build());
        }else{
            for(String strCareerDto : recruitmentPost.getCareers()) {
                EnumValidator<Career.AnnualLeave> annualLeaveEnumValidator = new EnumValidator<>();
                careers.add(Career.builder()
                        .recruitment(recruitment)
                        .annualLeave(annualLeaveEnumValidator.validateEnumString(strCareerDto, Career.AnnualLeave.class))
                        .build());
            }
        }
        Set<Image> subImages = new HashSet<>();
        for(String subImageUrl: recruitmentPost.getSubImage()) {
            Image subImage =imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(subImageUrl)).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
            );
            subImage.setIsRelated(true);
            subImages.add(subImage);
        }

        recruitment.setRecruitingJobSet(recruitingJobs);
        recruitment.setRecruitmentTypeSet(recruitmentTypes);
        recruitment.setEducationSet(educations);
        recruitment.setCareerSet(careers);
        recruitment.setSubImageSet(subImages);
        //recruitment.setRecruitmentContentType(recruitmentPost.getContentType());
        recruitmentRepository.save(recruitment);

        siteMapService.registerRecruitmentSiteMap(recruitment);
        return recruitment;
    }

    public List<Recruitment> getFilteredRecruitment(RecruitmentRequirementDto.Search search, Pageable pageable) {
        List<RecruitingJob.RecruitingJobName> recruitingJobList = new ArrayList<>();
        List<CompanyType.CompanyTypeName> companyTypeNameList = new ArrayList<>();
        List<RecruitmentType.RecruitmentTypeName> recruitmentTypeNameList = new ArrayList<>();
        List<Education.DEGREE> dgreeList = new ArrayList<>();
        List<Career.AnnualLeave> annualLeaveList = new ArrayList<>();
        List<RecruitmentAddress> recruitmentAddresses = new ArrayList<>();
        List<RecruitmentDeadlineType> recruitmentDeadlineTypeList = new ArrayList<>();

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
        if(!search.getRecruitmentDeadlineType().isEmpty()) {
            EnumValidator<RecruitmentDeadlineType> recruitmentDeadlineTypeEnumValidator = new EnumValidator<>();
            for (String recruitmentDeadlineTypeStr : search.getRecruitmentDeadlineType()) {
                RecruitmentDeadlineType recruitmentDeadlineType = recruitmentDeadlineTypeEnumValidator.validateEnumString(recruitmentDeadlineTypeStr, RecruitmentDeadlineType.class);
                recruitmentDeadlineTypeList.add(recruitmentDeadlineType);
            }
        } else recruitmentDeadlineTypeList.addAll(List.of(RecruitmentDeadlineType.values()));
        //마감된 공고 처리 true 면 아직 마감 안된 공고
        if(Objects.equals(search.getIsOpen(), "true")) {
            Specification<Recruitment> specification = Specification.where(RecruitmentSpecification.hasRecruitingJob(recruitingJobList))
                    .and(RecruitmentSpecification.hasCompanyType(companyTypeNameList))
                    .and(RecruitmentSpecification.hasRecruitmentType(recruitmentTypeNameList))
                    .and(RecruitmentSpecification.hasEducation(dgreeList))
                    .and(RecruitmentSpecification.hasCareer(annualLeaveList))
                    .and(RecruitmentSpecification.hasRecruitmentAddress(recruitmentAddresses))
                    .and(RecruitmentSpecification.hasDeadlineType(recruitmentDeadlineTypeList))
                    .and(RecruitmentSpecification.isOpenRecruitment())
                    ;
            return getOrder(search, pageable, specification);
        }else {
            Specification<Recruitment> specification = Specification.where(RecruitmentSpecification.hasRecruitingJob(recruitingJobList))
                    .and(RecruitmentSpecification.hasCompanyType(companyTypeNameList))
                    .and(RecruitmentSpecification.hasRecruitmentType(recruitmentTypeNameList))
                    .and(RecruitmentSpecification.hasEducation(dgreeList))
                    .and(RecruitmentSpecification.hasRecruitmentAddress(recruitmentAddresses))
                    .and(RecruitmentSpecification.hasDeadlineType(recruitmentDeadlineTypeList))
                    .and(RecruitmentSpecification.hasCareer(annualLeaveList));
            return getOrder(search, pageable, specification);
        }
    }
    public List<Recruitment> getOrder(RecruitmentRequirementDto.Search search, Pageable pageable, Specification<Recruitment> specification) {
        Sort sort = switch (search.getSortCondition()) {
            case "HITS" -> Sort.by("hits");
            case "DEADLINE" -> Sort.by("recruitmentDeadline");
            case "UPLOAD" -> Sort.by("uploadDate");
            default -> throw new BusinessException(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST);
        };

        if (!search.getSortCondition().equals("DEADLINE")) {
            if (search.getOrderBy().equals("DESC")) {
                sort = sort.descending();
            } else if (search.getOrderBy().equals("ASC")) {
                sort = sort.ascending();
            } else {
                throw new BusinessException(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST);
            }
        } else { //Deadline은 상시채용의 마감일이 2999년인 관계로 반대로 asceding 처리
            if (search.getOrderBy().equals("ASC")) {
                sort = Sort.by("recruitmentDeadline").descending();
            } else if (search.getOrderBy().equals("DESC")) {
                sort = Sort.by("recruitmentDeadline").ascending();
            } else {
                throw new BusinessException(ResponseErrorCode.SORT_CONDITION_BAD_REQUEST);
            }
        }

        PageRequest pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return recruitmentRepository.findAll(specification, pageableWithSort).stream().toList();
    }

    public void deleteRecruitmentByUid (String uid) {
        Recruitment recruitment = recruitmentRepository.findByUid(uid).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        imageService.deleteImageByUrl(recruitment.getMainImage().getImageUrl());
        recruitment.getSubImageSet().forEach(
                image -> imageService.deleteImageByUrl(image.getImageUrl())
        );

        siteMapService.deleteSiteMap(recruitment);
        recruitmentRepository.delete(recruitment);
    }
    public Recruitment addRecruitmentBookmark(String uid) {
        User user = userService.getLoginUser();
        Recruitment recruitment = findRecruitmentByUid(uid);

        if(bookmarkRepository.findByUserAndRecruitment(user,recruitment).isPresent()) {
            throw new BusinessException(ResponseErrorCode.BOOKMARK_ALREADY_EXIST);
        }
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .recruitment(recruitment)
                .build();
        user.getBookmarkSet().add(bookmark);
        recruitment.getBookmarkSet().add(bookmark);

        bookmarkRepository.save(bookmark);
        return recruitment;
    }
    public void removeRecruitmentBookMark(String uid) {
        User user = userService.getLoginUser();
        Recruitment recruitment = findRecruitmentByUid(uid);
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
    public List<Recruitment> getSearchRecruitment(String search, Pageable pageable) {
        return recruitmentRepository.findRecruitmentsByCompanyNameAndTitle(search,pageable).stream().toList();
    }
    public List<Recruitment> findAllNotInRecruitmentDeadlineTypes(List<RecruitmentDeadlineType> recruitmentDeadlineTypes) {
        return recruitmentRepository.findAll(RecruitmentSpecification.notInDeadlineTypes(recruitmentDeadlineTypes));
    }
    public List<Recruitment> findAllRecruitmentsWithDeadLineType(RecruitmentDeadlineType recruitmentDeadlineType) {
        return recruitmentRepository.findAllByRecruitmentDeadlineType(recruitmentDeadlineType);
    }

    public List<Recruitment> getRecruitmentsWithCareerAndDay(Integer day, List<Career.AnnualLeave> annualLeaves) {
        Set<Recruitment> resultList = new LinkedHashSet<>();
        LocalDateTime startOfToday = LocalDateTime.now().minusDays(day).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfToday = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        annualLeaves.forEach(
                annualLeave -> {
                    resultList.addAll(recruitmentRepository.findAllRecruitmentsDateRange(annualLeave,startOfToday, endOfToday));
                }
        );
        resultList.forEach(r->log.info(String.valueOf(r.getId())));
        return new ArrayList<>(resultList);
    }
}