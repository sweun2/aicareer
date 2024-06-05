package co.unlearning.aicareer.domain.common.user.service;


import co.unlearning.aicareer.domain.common.Image.Image;
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
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.EnumValidator;
import com.nimbusds.openid.connect.sdk.assurance.evidences.ElectronicSignatureEvidence;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;


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
    @Value("${front-url}")
    private String frontURL;
    private static final Random random = new Random();
    private static final List<String> NICKNAMES = List.of("스티브 잡스", "데니스 리치", "그레이스 호퍼", "앨런 튜링", "존 폰 노이만",
            "더글러스 엥겔바트", "존 백커스", "에이다 러브레이스", "이반 서덜랜드",
            "페르난도 코바토", "클로드 섀넌", "앨런 케이", "진 아메달", "앨런 퍼리스",
            "존 매카시", "린스 토닝", "로버트 노이스", "고든 무어", "빌 잉글리시",
            "게리 킬달", "오틀리 바이어스", "톰 킬번", "에드거 코드", "도널드 데이비스",
            "켄 올슨", "헨리 에드워즈 로버츠", "제이 프레슬퍼바", "블리스 코헨",
            "에드 로버츠", "안나 안토니아치", "장 사무엘 로", "게르하르트 브링크만",
            "사무엘 모스", "찰스 배비지", "존 앳터내스오프", "클리포드 베리",
            "콘래드 주세", "프레더릭 틸덴", "존 클라크", "마빈 민스키", "하랄드 반 존",
            "프레드 브룩스", "유진 아밍턴", "카를 폰 프리시", "시모어 크레이",
            "프란시스 스펜서", "제이 라이트 포리스트", "조셉 와이젤바움",
            "필립 베크먼", "매니엘 블럼", "아브람 포트", "허브 그로스", "제임스 골드스타인",
            "나단 바틀레트", "앤드류 부스", "레이 노이블", "리처드 블록", "토머스 플라워스",
            "노먼 브리지", "잭 킬비", "에드슨 드 구즈만", "해럴드 로젠", "제임스 리처드슨",
            "로버트 브룩", "윌리엄 쇼클리", "윌리스 호킨스", "존 빈센트 아타나소프",
            "에리크 사무엘", "존 브레이너드", "어네스트 애플", "해럴드 베어드", "조지 애플턴",
            "로버트 해링턴", "제임스 와즈니아크", "칼 헬너", "앨런 티모시", "월터 보쉬",
            "레너드 클라인록", "존 테이틀러", "해럴드 에반스", "리처드 해밀턴",
            "도날드 미첼", "필립 플린", "제롬 루빈", "아론 스와츠", "폴 바란",
            "잭 맥카퍼티", "존 파스트", "프랭크 로즈", "도날드 크누스", "조셉 칼",
            "윌리엄 브래드포드", "루이스 스몰우드", "잭 트램멜", "고든 벨", "제임스 그레이",
            "로버트 카", "존 헤지스", "피터 패커드", "하워드 에이컨", "토머스 왓슨");

    public String generateUniqueNickname() {
        String nickname = generateRandomNickname();

        while (userRepository.findByNickname(nickname).isPresent()) {
            nickname = generateRandomNickname();
        }

        return nickname;
    }

    private String generateRandomNickname() {
        String randomNickname = NICKNAMES.get(random.nextInt(NICKNAMES.size()));
        int randomNumber = 10000 + random.nextInt(90000); // 5자리 랜덤 숫자 생성
        return randomNickname + " " + randomNumber;
    }
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.USER_NOT_FOUND)
        );
    }
    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().equals("anonymousUser")){
            throw new BusinessException(ResponseErrorCode.USER_UNAUTHORIZED);
        }
        User user = (User) authentication.getPrincipal();
        return userRepository.findById(user.getId()).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.USER_NOT_FOUND));
    }
    public Boolean isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !authentication.getPrincipal().equals("anonymousUser");
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
        if(!Objects.equals(frontURL, "http://localhost:3000")) {
            ResponseCookie accessToken = ResponseCookie.from("_aT","")
                    .path("/")
                    .sameSite("None")
                    .domain(".aicareer.co.kr")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", accessToken.toString());

            ResponseCookie refreshToken = ResponseCookie.from("_rT","")
                    .path("/")
                    .sameSite("None")
                    .domain(".aicareer.co.kr")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", refreshToken.toString());
        } else {
            ResponseCookie accessToken = ResponseCookie.from("_aT","")
                    .path("/")
                    .sameSite("None")
                    .domain("localhost:3000")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", accessToken.toString());

            ResponseCookie refreshToken = ResponseCookie.from("_rT","")
                    .path("/")
                    .sameSite("None")
                    .domain("localhost:3000")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(24*60*60)
                    .build();
            response.addHeader("Set-Cookie", refreshToken.toString());
        }
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
    public User updateUserInfo(UserRequestDto.UserData userData) {
        User user = getLoginUser();
        if(userData.getNickname() != null && !Objects.equals(userData.getNickname(), user.getNickname())) {
            if(!userData.getNickname().equals(getLoginUser().getNickname()) && userRepository.findByNickname(userData.getNickname()).isPresent())
                throw new BusinessException(ResponseErrorCode.USER_NICKNAME_DUPLICATE);

            user.setNickname(userData.getNickname());
        }
        if(userData.getProfileImageUrl() != null) {
            if(user.getProfileImage() == null) {
                user.setProfileImage(Image.builder()
                                .createdDate(LocalDateTime.now())
                                .isRelated(true)
                                .imageUrl(ImagePathLengthConverter.slicingImagePathLength(userData.getProfileImageUrl()))
                        .build());
            }
            else {
                user.getProfileImage().setIsRelated(false);

                user.setProfileImage(Image.builder()
                        .id(user.getProfileImage().getId())
                        .createdDate(LocalDateTime.now())
                        .isRelated(true)
                        .imageUrl(ImagePathLengthConverter.slicingImagePathLength(userData.getProfileImageUrl()))
                        .build());
            }
        }
        return userRepository.save(user);
    }
}
