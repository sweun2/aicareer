package co.unlearning.aicareer.domain.job.recruitment.service;

import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
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
import co.unlearning.aicareer.domain.job.recruitmentImage.RecruitmentImage;
import co.unlearning.aicareer.domain.job.recruitmentImage.repository.RecruitmentImageRepository;
import co.unlearning.aicareer.domain.job.recruitmentImage.service.RecruitmentImageService;
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
import jakarta.persistence.EntityManager;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;


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

    private final RecruitmentImageService recruitmentImageService;
    private final RecruitmentImageRepository recruitmentImageRepository;
    private final EntityManager entityManager;
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

        // Update company
        Company company = companyService.addNewCompany(
                CompanyRequirementDto.CompanyInfo.builder()
                        .companyAddress(recruitmentPost.getCompanyAddress())
                        .companyName(recruitmentPost.getCompanyName())
                        .companyType(recruitmentPost.getCompanyType())
                        .build());
        recruitment.setCompany(company);

        // Update main image
        updateMainImage(recruitmentPost.getMainImage(), recruitment.getMainImage(), recruitment::setMainImage);

        // Update sub images
        updateSubImages(recruitment, recruitmentPost.getSubImage());

        // Update sets
        recruitment.setRecruitingJobSet(updateRecruitingJobs(recruitment, recruitmentPost.getRecruitingJobNames()));
        recruitment.setRecruitmentTypeSet(updateRecruitmentTypes(recruitment, recruitmentPost.getRecruitmentTypeNames()));
        recruitment.setEducationSet(updateEducations(recruitment, recruitmentPost.getEducations()));
        recruitment.setCareerSet(updateCareers(recruitment, recruitmentPost.getCareers()));

        // Update other fields
        recruitment.setRecruitmentStartDate(LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentStartDate()));
        recruitment.setRecruitmentDeadlineType(new EnumValidator<RecruitmentDeadlineType>()
                .validateEnumString(recruitmentPost.getRecruitmentDeadline().getDeadlineType(), RecruitmentDeadlineType.class));
        updateRecruitmentDeadline(recruitment, recruitmentPost.getRecruitmentDeadline());
        recruitment.setRecruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink());
        recruitment.setRecruitmentAddress(new EnumValidator<RecruitmentAddress>()
                .validateEnumString(recruitmentPost.getRecruitmentAddress(), RecruitmentAddress.class));
        recruitment.setTitle(recruitmentPost.getTitle());
        recruitment.setContent(recruitmentPost.getContent());
        recruitment.setLastModified(LocalDateTime.now());

        // Save recruitment
        recruitmentRepository.save(recruitment);

        // Register recruitment to site map
        siteMapService.registerRecruitmentSiteMap(recruitment);

        return recruitment;
    }

    private void updateMainImage(String newImageUrl, RecruitmentImage currentRecruitmentImage, Consumer<RecruitmentImage> setRecruitmentImage) {
        if (newImageUrl != null && !newImageUrl.isEmpty()) {
            Image newImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(newImageUrl))
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            if (currentRecruitmentImage != null && !newImage.getImageUrl().equals(currentRecruitmentImage.getImage().getImageUrl())) {
                currentRecruitmentImage.getImage().setIsRelated(false);
                recruitmentImageService.removeRecruitmentImage(currentRecruitmentImage);

                RecruitmentImage recruitmentImage = RecruitmentImage.builder()
                        .imageOrder(0)
                        .image(newImage)
                        .recruitment(currentRecruitmentImage.getRecruitment())
                        .build();
                newImage.setIsRelated(true);
                setRecruitmentImage.accept(recruitmentImage);
                recruitmentImageRepository.save(recruitmentImage);
            }
        } else if (currentRecruitmentImage != null) {
            currentRecruitmentImage.getImage().setIsRelated(false);
            recruitmentImageService.removeRecruitmentImage(currentRecruitmentImage);
            setRecruitmentImage.accept(null);
            entityManager.flush();
        }
    }

    private void updateSubImages(Recruitment recruitment, List<String> newSubImageUrls) {
        List<RecruitmentImage> currentRecruitmentImages = recruitment.getSubImages().stream()
                .filter(boardImage -> boardImage.getImageOrder() != null && boardImage.getImageOrder() != 0)
                .collect(Collectors.toList());

        if (!currentRecruitmentImages.isEmpty()) {
            currentRecruitmentImages.forEach(bi -> {
                bi.getImage().setIsRelated(false);
                imageService.deleteImageByUrl(bi.getImage().getImageUrl());
            });
            recruitmentImageRepository.deleteAll(currentRecruitmentImages);
            entityManager.flush();
        }

        recruitment.getSubImages().removeAll(currentRecruitmentImages);
        recruitmentRepository.save(recruitment);

        for (int order = 0; order < newSubImageUrls.size(); order++) {
            String imageUrl = newSubImageUrls.get(order);
            String slicedImageUrl = ImagePathLengthConverter.slicingImagePathLength(imageUrl);
            Image image = imageRepository.findByImageUrl(slicedImageUrl)
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            Optional<RecruitmentImage> recruitmentImageOptional = recruitmentImageRepository.findByImage(image);
            if (recruitmentImageOptional.isEmpty()) {
                RecruitmentImage newRecruitmentImage = RecruitmentImage.builder()
                        .recruitment(recruitment)
                        .image(image)
                        .imageOrder(order + 1)
                        .build();
                image.setIsRelated(true);
                recruitment.getSubImages().add(newRecruitmentImage);
                recruitmentImageRepository.save(newRecruitmentImage);
            } else {
                recruitmentImageOptional.get().setImageOrder(order + 1);
                recruitmentImageRepository.save(recruitmentImageOptional.get());
            }
        }

        recruitmentRepository.save(recruitment);
    }

    private Set<RecruitingJob> updateRecruitingJobs(Recruitment recruitment, List<String> jobNames) {
        return jobNames.stream()
                .map(jobName -> RecruitingJob.builder()
                        .recruitJobName(new EnumValidator<RecruitingJob.RecruitingJobName>()
                                .validateEnumString(jobName, RecruitingJob.RecruitingJobName.class))
                        .recruitment(recruitment)
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<RecruitmentType> updateRecruitmentTypes(Recruitment recruitment, List<String> typeNames) {
        return typeNames.stream()
                .map(typeName -> RecruitmentType.builder()
                        .recruitment(recruitment)
                        .recruitmentTypeName(new EnumValidator<RecruitmentType.RecruitmentTypeName>()
                                .validateEnumString(typeName, RecruitmentType.RecruitmentTypeName.class))
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Education> updateEducations(Recruitment recruitment, List<String> educations) {
        if (educations.isEmpty()) {
            return Collections.singleton(Education.builder()
                    .recruitment(recruitment)
                    .degree(Education.DEGREE.IRRELEVANCE)
                    .build());
        }
        return educations.stream()
                .map(education -> Education.builder()
                        .recruitment(recruitment)
                        .degree(new EnumValidator<Education.DEGREE>()
                                .validateEnumString(education, Education.DEGREE.class))
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Career> updateCareers(Recruitment recruitment, List<String> careers) {
        if (careers.isEmpty()) {
            return Collections.singleton(Career.builder()
                    .recruitment(recruitment)
                    .annualLeave(Career.AnnualLeave.IRRELEVANCE)
                    .build());
        }
        return careers.stream()
                .map(career -> Career.builder()
                        .recruitment(recruitment)
                        .annualLeave(new EnumValidator<Career.AnnualLeave>()
                                .validateEnumString(career, Career.AnnualLeave.class))
                        .build())
                .collect(Collectors.toSet());
    }

    private void updateRecruitmentDeadline(Recruitment recruitment, RecruitmentRequirementDto.RecruitmentDeadLine deadline) {
        RecruitmentDeadlineType deadlineType = recruitment.getRecruitmentDeadlineType();
        if (deadlineType.equals(RecruitmentDeadlineType.DUE_DATE)) {
            recruitment.setRecruitmentDeadline(LocalDateTimeStringConverter.StringToLocalDateTime(deadline.getRecruitmentDeadline()));
        } else if (deadlineType.equals(RecruitmentDeadlineType.EXPIRED)) {
            recruitment.setRecruitmentDeadline(LocalDateTime.of(2000,1,1, 1, 1));
        } else {
            recruitment.setRecruitmentDeadline(LocalDateTime.of(2999,12,12,12,12));
        }
    }


    public Recruitment addRecruitmentPost(RecruitmentRequirementDto.RecruitmentPost recruitmentPost) throws Exception {
        // Check if the company is registered
        Company company = companyRepository.findByCompanyName(recruitmentPost.getCompanyName())
                .orElseGet(() -> companyService.addNewCompany(
                        CompanyRequirementDto.CompanyInfo.builder()
                                .companyName(recruitmentPost.getCompanyName())
                                .companyAddress(recruitmentPost.getCompanyAddress())
                                .companyType(recruitmentPost.getCompanyType())
                                .build())
                );

        // Set recruitment start date
        LocalDateTime startDate = (recruitmentPost.getRecruitmentStartDate() == null) ?
                LocalDateTime.now() :
                LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentStartDate());

        // Validate and set recruitment deadline
        RecruitmentDeadlineType deadlineType = new EnumValidator<RecruitmentDeadlineType>()
                .validateEnumString(recruitmentPost.getRecruitmentDeadline().getDeadlineType(), RecruitmentDeadlineType.class);

        LocalDateTime deadline = (deadlineType == RecruitmentDeadlineType.DUE_DATE) ?
                LocalDateTimeStringConverter.StringToLocalDateTime(recruitmentPost.getRecruitmentDeadline().getRecruitmentDeadline()) :
                LocalDateTime.of(2999,12,12,12,12);

        if (deadlineType == RecruitmentDeadlineType.DUE_DATE) {
            TimeValidator.RemainingTimeValidator(deadline);
        }

        // Validate and set recruitment address
        RecruitmentAddress recruitmentAddress = new EnumValidator<RecruitmentAddress>()
                .validateEnumString(recruitmentPost.getRecruitmentAddress(), RecruitmentAddress.class);
        // Validate text type
        Recruitment.TextType textType = new EnumValidator<Recruitment.TextType>()
                .validateEnumString(recruitmentPost.getTextType(), Recruitment.TextType.class);

        // Build recruitment object
        Recruitment recruitment = Recruitment.builder()
                .uid(UUID.randomUUID().toString())
                .company(company)
                .recruitmentStartDate(startDate)
                .recruitmentDeadlineType(deadlineType)
                .recruitmentDeadline(deadline)
                .uploadDate(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .recruitmentAnnouncementLink(recruitmentPost.getRecruitmentAnnouncementLink())
                .content(recruitmentPost.getContent())
                .recruitmentAddress(recruitmentAddress)
                .title(recruitmentPost.getTitle())
                .textType(textType)
                .hits(0)
                .build();

        // Set main image if provided
        if (recruitmentPost.getMainImage() != null) {
            Image mainImage = imageRepository.findByImageUrl(
                            ImagePathLengthConverter.slicingImagePathLength(recruitmentPost.getMainImage()))
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            mainImage.setIsRelated(true);
            recruitment.setMainImage(
                    RecruitmentImage.builder()
                            .image(mainImage)
                            .recruitment(recruitment)
                            .imageOrder(0)
                            .build()
            );
        }

        // Set recruiting jobs
        Set<RecruitingJob> recruitingJobs = recruitmentPost.getRecruitingJobNames().stream()
                .map(jobName -> RecruitingJob.builder()
                        .recruitJobName(new EnumValidator<RecruitingJob.RecruitingJobName>()
                                .validateEnumString(jobName, RecruitingJob.RecruitingJobName.class))
                        .recruitment(recruitment)
                        .build())
                .collect(Collectors.toSet());

        // Set recruitment types
        Set<RecruitmentType> recruitmentTypes = recruitmentPost.getRecruitmentTypeNames().stream()
                .map(typeName -> RecruitmentType.builder()
                        .recruitment(recruitment)
                        .recruitmentTypeName(new EnumValidator<RecruitmentType.RecruitmentTypeName>()
                                .validateEnumString(typeName, RecruitmentType.RecruitmentTypeName.class))
                        .build())
                .collect(Collectors.toSet());

        // Set educations
        Set<Education> educations = (recruitmentPost.getEducations().isEmpty()) ?
                Collections.singleton(Education.builder()
                        .recruitment(recruitment)
                        .degree(Education.DEGREE.IRRELEVANCE)
                        .build()) :
                recruitmentPost.getEducations().stream()
                        .map(education -> Education.builder()
                                .recruitment(recruitment)
                                .degree(new EnumValidator<Education.DEGREE>()
                                        .validateEnumString(education, Education.DEGREE.class))
                                .build())
                        .collect(Collectors.toSet());

        // Set careers
        Set<Career> careers = (recruitmentPost.getCareers().isEmpty()) ?
                Collections.singleton(Career.builder()
                        .recruitment(recruitment)
                        .annualLeave(Career.AnnualLeave.IRRELEVANCE)
                        .build()) :
                recruitmentPost.getCareers().stream()
                        .map(career -> Career.builder()
                                .recruitment(recruitment)
                                .annualLeave(new EnumValidator<Career.AnnualLeave>()
                                        .validateEnumString(career, Career.AnnualLeave.class))
                                .build())
                        .collect(Collectors.toSet());

        // Set sub images
        List<RecruitmentImage> subImages = new ArrayList<>();
        int order = 0;
        for (String subImageUrl : recruitmentPost.getSubImage()) {
            Image subImage = imageRepository.findByImageUrl(
                            ImagePathLengthConverter.slicingImagePathLength(subImageUrl))
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            subImage.setIsRelated(true);
            subImages.add(RecruitmentImage.builder()
                    .image(subImage)
                    .recruitment(recruitment)
                    .imageOrder(++order)
                    .build());
        }

        // Set relationships
        recruitment.setRecruitingJobSet(recruitingJobs);
        recruitment.setRecruitmentTypeSet(recruitmentTypes);
        recruitment.setEducationSet(educations);
        recruitment.setCareerSet(careers);
        recruitment.setSubImages(subImages);

        // Save recruitment
        recruitmentRepository.save(recruitment);

        // Register recruitment to site map
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
        //recruitmentImageService.removeRecruitmentImage(recruitment.getMainImage());
        recruitment.getSubImages().forEach(
                recruitmentImageService::removeRecruitmentImage
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