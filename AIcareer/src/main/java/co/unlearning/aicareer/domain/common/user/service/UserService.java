package co.unlearning.aicareer.domain.common.user.service;


import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.common.user.UserTerms;
import co.unlearning.aicareer.domain.common.user.dto.UserRequestDto;
import co.unlearning.aicareer.domain.common.user.repository.UserInterestRepository;
import co.unlearning.aicareer.domain.common.user.repository.UserRepository;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.common.user.repository.UserTermsRepository;
import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.global.security.jwt.TokenService;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
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
    private final TokenService tokenService;
    private final UserInterestRepository userInterestRepository;
    private final UserTermsRepository userTermsRepository;
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
                .domain("aicareer.co.kr")
                .httpOnly(true)
                .secure(true)
                .maxAge(24*60*60)
                .build();
        response.addHeader("Set-Cookie", accessToken.toString());

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken","")
                .path("/")
                .sameSite("None")
                .domain("aicareer.co.kr")
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
        if(user.getIsAgreePrivacyTerms()==null) {
            UserTerms isAgreePrivacyTerms = UserTerms.builder()
                    .isAgree(userTermsInfo.getIsAgreePrivacyTerms())
                    .user(user)
                    .agreeDate(LocalDateTime.now())
                    .build();
            userTermsRepository.save(isAgreePrivacyTerms);
            user.setIsAgreePrivacyTerms(isAgreePrivacyTerms);
        }
        if(user.getIsAgreeUseTerms()==null) {
            UserTerms isAgreeUseTerms = UserTerms.builder()
                    .isAgree(userTermsInfo.getIsAgreeUseTerms())
                    .user(user)
                    .agreeDate(LocalDateTime.now())
                    .build();
            userTermsRepository.save(isAgreeUseTerms);
            user.setIsAgreeUseTerms(isAgreeUseTerms);
        }
        if(user.getIsMarketing()==null) {
            UserTerms isMarketing = UserTerms.builder()
                    .isAgree(userTermsInfo.getIsMarketing())
                    .user(user)
                    .agreeDate(LocalDateTime.now())
                    .build();
            userTermsRepository.save(isMarketing);
            user.setIsMarketing(isMarketing);
        } else {
            user.getIsMarketing().setIsAgree(userTermsInfo.getIsMarketing());
            user.getIsMarketing().setAgreeDate(LocalDateTime.now());
        }
        if(user.getIsInformationTerms()==null) {
            UserTerms isInformationTerms = UserTerms.builder()
                    .isAgree(userTermsInfo.getIsInformationTerms())
                    .user(user)
                    .agreeDate(LocalDateTime.now())
                    .build();
            userTermsRepository.save(isInformationTerms);
            user.setIsMarketing(isInformationTerms);
        } else {
            user.getIsInformationTerms().setIsAgree(userTermsInfo.getIsMarketing());
            user.getIsInformationTerms().setAgreeDate(LocalDateTime.now());
        }
        return userRepository.save(user);
    }
    public UserInterest updateUserInterest(UserRequestDto.UserInterestInfo userInterestInfo) {
        User user = getLoginUser();
        if(user.getIsInterest()) {
            userInterestRepository.delete(user.getUserInterest());
        }
        UserInterest userInterest = new UserInterest();
        user.setIsInterest(true);

        Set<CompanyType> companyTypeSet = new HashSet<>();
        EnumValidator<CompanyType.CompanyTypeName> companyTypeNameEnumValidator = new EnumValidator<>();
        for (String companyTypeNameStr : userInterestInfo.getCompanyTypes()) {
            companyTypeSet.add(CompanyType.builder()
                    .userInterest(userInterest)
                    .companyTypeName(companyTypeNameEnumValidator.validateEnumString(companyTypeNameStr, CompanyType.CompanyTypeName.class))
                    .build());
        }
        Set<RecruitingJob> recruitingJobSet = new HashSet<>();
        EnumValidator<RecruitingJob.RecruitingJobName> recruitingJobNameEnumValidator = new EnumValidator<>();
        for (String recruitingJobNameStr : userInterestInfo.getRecruitingJobNames()) {
            recruitingJobSet.add(RecruitingJob.builder()
                    .userInterest(userInterest)
                    .recruitJobName(recruitingJobNameEnumValidator.validateEnumString(recruitingJobNameStr, RecruitingJob.RecruitingJobName.class))
                    .build());
        }
        Set<Education> educationSet = new HashSet<>();
        EnumValidator<Education.DEGREE> degreeEnumValidator = new EnumValidator<>();
        for (String degreeStr : userInterestInfo.getEducations()) {
            educationSet.add(Education.builder()
                    .userInterest(userInterest)
                    .degree(degreeEnumValidator.validateEnumString(degreeStr, Education.DEGREE.class))
                    .build());
        }
        userInterest.getEducationSet().clear();
        userInterest.setEducationSet(educationSet);

        userInterest.getCompanyTypeSet().clear();
        userInterest.setCompanyTypeSet(companyTypeSet);

        userInterest.getRecruitingJobSet().clear();
        userInterest.setRecruitingJobSet(recruitingJobSet);

        userInterest.setIsMetropolitanArea(userInterestInfo.getIsMetropolitanArea());
        userInterest.setUser(user);
        userRepository.save(user);
        return userInterestRepository.save(userInterest);
    }
    public UserInterest getUserInterest() {
        User user = getLoginUser();
       if(user.getUserInterest()==null)
           throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
       return user.getUserInterest();
    }
}
