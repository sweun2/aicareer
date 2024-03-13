package co.unlearning.aicareer.domain.common.user.dto;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.common.user.UserTerms;
import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSimple {
        private Integer userId;
        private String name;
        private String nickname;
        public static UserSimple of(User user) {
            return UserSimple.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .build();
        }

        public static List<UserSimple> of(List<User> users) {
            return users.stream().map(UserSimple::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String nickname;
        private String name;
        private String email;
        private String joinDate;
        private UserRole userRole;
        private UserTermsInfo isMarketing;
        private UserTermsInfo isInformationTerms;
        private UserTermsInfo isAgreeUseTerms;
        private UserTermsInfo isAgreePrivacyTerms;

        private Boolean isInterest;
        public static UserInfo of(User user) {
            return UserInfo.builder()
                    .nickname(user.getNickname())
                    .name(user.getName())
                    .email(user.getEmail())
                    .joinDate(String.valueOf(user.getJoinDate()))
                    .userRole(user.getUserRole())
                    .isMarketing(UserTermsInfo.of(user.getIsMarketing()))
                    .isInformationTerms(UserTermsInfo.of(user.getIsAgreeInformationTerms()))
                    .isAgreeUseTerms(UserTermsInfo.of(user.getIsAgreeUseTerms()))
                    .isAgreePrivacyTerms(UserTermsInfo.of(user.getIsAgreePrivacyTerms()))
                    .isInterest(user.getIsInterest())
                    .build();
        }

        public static List<UserInfo> of(List<User> users) {
            return users.stream().map(UserInfo::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInterestInfo {
        private UserSimple userSimple;
        private Set<Education> educationSet;
        private Set<RecruitingJob> recruitingJobSet;
        private Set<CompanyType> companyTypeSet;
        private Boolean isMetropolitanArea;
        public static UserInterestInfo of(UserInterest userInterest) {
            return UserInterestInfo.builder()
                    .userSimple(UserSimple.of(userInterest.getUser()))
                    .educationSet(userInterest.getEducationSet())
                    .recruitingJobSet(userInterest.getRecruitingJobSet())
                    .companyTypeSet(userInterest.getCompanyTypeSet())
                    .isMetropolitanArea(userInterest.getIsMetropolitanArea())
                    .build();
        }

        public static List<UserInterestInfo> of(List<UserInterest> userInterests) {
            return userInterests.stream().map(UserInterestInfo::of).collect(Collectors.toList());
        }
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTermsInfo {
        private Boolean isAgree;
        private String agreeDate;
        private static  UserTermsInfo of(UserTerms userTerms) {
            return UserTermsInfo.builder()
                    .agreeDate(LocalDateTimeStringConverter.LocalDateTimeToString(userTerms.getAgreeDate()))
                    .isAgree(userTerms.getIsAgree())
                    .build();
        }
    }
}

