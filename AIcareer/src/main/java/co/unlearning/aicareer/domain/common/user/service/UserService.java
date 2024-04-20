package co.unlearning.aicareer.domain.common.user.service;


import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.common.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.common.user.repository.UserInterestRepository;
import co.unlearning.aicareer.domain.common.user.repository.UserRepository;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.companytype.repository.CompanyTypeRepository;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.education.repository.EducationRepository;
import co.unlearning.aicareer.domain.job.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.job.recruitmenttype.repository.RecruitmentTypeRepository;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.job.recrutingjob.repository.RecruitingJobRepository;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicSignatureEvidence;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserInterestRepository userInterestRepository;
    private final RecruitingJobRepository recruitingJobRepository;
    private final EducationRepository educationRepository;
    private final CompanyTypeRepository companyTypeRepository;
    private final EntityManager entityManager;
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.USER_NOT_FOUND)
        );
    }
    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            log.info("annnoy");
            throw new BusinessException(ResponseErrorCode.USER_UNAUTHORIZED);
        }
        User user = (User) authentication.getPrincipal();
        log.info("getLoginUser");

        return userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.USER_NOT_FOUND));
    }

    public Boolean verifyLoginUser(User user) {
        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.USER_NOT_FOUND)
        );

        if (!user1.getEmail().equals(user.getEmail())) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_FOUND);
        } else if (!user1.getPassword().equals(user.getPassword())) {
            throw new BusinessException(ResponseErrorCode.USER_UNAUTHORIZED);
        } else return true;
    }
    public User updateUserRole(UserRequestDto.UserRole userRole) {
        User user = getLoginUser();
        if(user.getUserRole() != UserRole.ADMIN) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }
        try {
            user.setUserRole(UserRole.valueOf(userRole.getUserRole()));
            userRepository.save(user);
        }catch (Exception e) {
            log.info(e.getMessage());
        }
        return user;
    }
    public void logout(HttpServletResponse response) {
        ResponseCookie accessToken = ResponseCookie.from("accessToken","")
                .path("/")
                .sameSite("None")
                .domain(".aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie", accessToken.toString());

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken","")
                .path("/")
                .sameSite("None")
                .domain(".aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie", refreshToken.toString());

    }
    public void checkAdmin() {
        User user = getLoginUser();
        if(user.getUserRole()!=UserRole.ADMIN)
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
    }
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
    public User updateUserTerms(UserRequestDto.UserTermsInfo userTermsInfo) {
        User user = getLoginUser();
        log.info(String.valueOf(userTermsInfo.getIsInformationTerms()));
        log.info(String.valueOf(userTermsInfo.getIsMarketing()));

        if(userTermsInfo.getIsMarketing()!=null) {
            user.getIsMarketing().setIsAgree(userTermsInfo.getIsMarketing());
            user.getIsMarketing().setAgreeDate(LocalDateTime.now());
        }
        if(userTermsInfo.getIsInformationTerms()!=null) {
            user.getIsAgreeInformationTerms().setIsAgree(userTermsInfo.getIsInformationTerms());
            user.getIsAgreeInformationTerms().setAgreeDate(LocalDateTime.now());
        }

        return userRepository.save(user);
    }
    public UserInterest updateUserInterest(UserRequestDto.UserInterestInfo userInterestInfo) {
        User user = getLoginUser();
        if (user.getUserInterest() != null && user.getIsInterest()) {
            UserInterest userInterest = user.getUserInterest();
            if(userInterest.getRecruitingJobSet() != null) {
                recruitingJobRepository.deleteAll(userInterest.getRecruitingJobSet());
            }
            if(userInterest.getCompanyTypeSet() != null) {
                companyTypeRepository.deleteAll(userInterest.getCompanyTypeSet());
            }
            if(userInterest.getEducationSet() != null) {
                educationRepository.deleteAll(userInterest.getEducationSet());
            }
            entityManager.flush();
            entityManager.clear();

            /*EnumValidator<CompanyType.CompanyTypeName> companyTypeNameEnumValidator = new EnumValidator<>();
            userInterestInfo.getCompanyTypes().forEach(companyTypeNameStr -> {
                CompanyType.CompanyTypeName companyTypeName = companyTypeNameEnumValidator.validateEnumString(companyTypeNameStr, CompanyType.CompanyTypeName.class);
                userInterest.getCompanyTypeSet().add(CompanyType.builder()
                        .userInterest(userInterest)
                        .companyTypeName(companyTypeName)
                        .build());
            });
            EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobNameEnumValidator = new EnumValidator<>();
            userInterestInfo.getRecruitingJobNames().forEach(recruitingJobNameStr -> {
                RecruitingJob.RecruitingJobName recruitingJobName = recruitingJobNameEnumValidator.validateEnumString(recruitingJobNameStr, RecruitingJob.RecruitingJobName.class);
                userInterest.getRecruitingJobSet().add(RecruitingJob.builder()
                        .userInterest(userInterest)
                        .recruitJobName(recruitingJobName)
                        .build());
            });
            EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
            userInterestInfo.getEducations().forEach(degreeStr -> {
                Education.DEGREE degree = degreeEnumValidator.validateEnumString(degreeStr, Education.DEGREE.class);
                userInterest.getEducationSet().add(Education.builder()
                        .userInterest(userInterest)
                        .degree(degree)
                        .build());
            });
            userInterest.setIsMetropolitanArea(userInterestInfo.getIsMetropolitanArea());
            userInterest.setReceiveEmail(userInterestInfo.getReceivedEmail());
            user.setUserInterest(userInterest);

            userInterestRepository.save(userInterest);
            return userInterest;*/
        }
        UserInterest userInterest = new UserInterest();

        Set<CompanyType> companyTypeSet = new HashSet<>();
        EnumValidator<CompanyType.CompanyTypeName> companyTypeNameEnumValidator = new EnumValidator<>();
        for (String companyTypeNameStr : userInterestInfo.getCompanyTypes()) {
            CompanyType.CompanyTypeName companyTypeName = companyTypeNameEnumValidator.validateEnumString(companyTypeNameStr, CompanyType.CompanyTypeName.class);
            companyTypeSet.add(CompanyType.builder()
                    .userInterest(userInterest)
                    .companyTypeName(companyTypeName)
                    .build());
        }
        Set<RecruitingJob> recruitingJobSet = new HashSet<>();
        EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobNameEnumValidator = new EnumValidator<>();
        for (String recruitingJobNameStr : userInterestInfo.getRecruitingJobNames()) {
            RecruitingJob.RecruitingJobName recruitingJobName = recruitingJobNameEnumValidator.validateEnumString(recruitingJobNameStr, RecruitingJob.RecruitingJobName.class);
            recruitingJobSet.add(RecruitingJob.builder()
                    .userInterest(userInterest)
                    .recruitJobName(recruitingJobName)
                    .build());
        }
        Set<Education> educationSet = new HashSet<>();
        EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
        for (String degreeStr : userInterestInfo.getEducations()) {
            Education.DEGREE degree = degreeEnumValidator.validateEnumString(degreeStr, Education.DEGREE.class);
            educationSet.add(Education.builder()
                    .userInterest(userInterest)
                    .degree(degree)
                    .build());
        }

        userInterest.setEducationSet(educationSet);
        userInterest.setCompanyTypeSet(companyTypeSet);
        userInterest.setRecruitingJobSet(recruitingJobSet);
        userInterest.setIsMetropolitanArea(userInterestInfo.getIsMetropolitanArea());
        userInterest.setReceiveEmail(userInterestInfo.getReceivedEmail());
        userInterestRepository.save(userInterest);

        user.setUserInterest(userInterest);
        user.setIsInterest(true);
        userRepository.save(user);
        return userInterest;
    }
    public User setUserInterest(UserRequestDto.UserInterestInfo userInterestInfo) {
        updateUserInterest(userInterestInfo);
        return getLoginUser();
    }
    public UserInterest getUserInterest() {
        User user = getLoginUser();
       if(user.getUserInterest()==null)
           throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
       return user.getUserInterest();
    }
}
