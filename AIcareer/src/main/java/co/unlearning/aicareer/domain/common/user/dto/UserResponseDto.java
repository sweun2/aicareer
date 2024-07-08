package co.unlearning.aicareer.domain.common.user.dto;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.common.user.UserTerms;
import co.unlearning.aicareer.domain.job.companytype.dto.CompanyTypeResponseDto;
import co.unlearning.aicareer.domain.job.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.job.recrutingjob.dto.RecruitingJobResponseDto;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static co.unlearning.aicareer.global.utils.EncodeUtil.encodeWithHmacSHA256;


public class UserResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSimple {
        private Integer userId;
        private String nickname;
        private String profileImageUrl;
        private String hashedChannelTalkMemberId;
        public static UserSimple of(User user) {
            if(user == null) return UserSimple.builder().build();
            return UserSimple.builder()
                    .hashedChannelTalkMemberId(encodeWithHmacSHA256(user.getId().toString()))
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImage() != null ? ImagePathLengthConverter.extendImagePathLength(user.getProfileImage().getImageUrl()) : StringUtils.EMPTY)
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
        private Integer userId;
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
        private String profileImageUrl;
        private String hashedChannelTalkMemberId;

        public static UserInfo of(User user) {
            if(user == null ) return UserInfo.builder().build();
            return UserInfo.builder()
                    .hashedChannelTalkMemberId(encodeWithHmacSHA256(user.getId().toString()))
                    .userId(user.getId())
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
                    .profileImageUrl(user.getProfileImage() != null ? ImagePathLengthConverter.extendImagePathLength(user.getProfileImage().getImageUrl()) : StringUtils.EMPTY)
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
        private List<EducationResponseDto.EducationDto> educationDtos;
        private List<RecruitingJobResponseDto.RecruitingJobNameDto> recruitingJobNames;
        private List<CompanyTypeResponseDto.CompanyTypeInfo> companyTypeInfos;
        private Boolean isMetropolitanArea;
        private String receivedEmail;
        public static UserInterestInfo of(UserInterest userInterest) {
            return UserInterestInfo.builder()
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNameDto.of(List.copyOf(userInterest.getRecruitingJobSet())))
                    .educationDtos(EducationResponseDto.EducationDto.of(List.copyOf(userInterest.getEducationSet())))
                    .companyTypeInfos(CompanyTypeResponseDto.CompanyTypeInfo.of(List.copyOf(userInterest.getCompanyTypeSet())))
                    .isMetropolitanArea(userInterest.getIsMetropolitanArea())
                    .receivedEmail(userInterest.getReceiveEmail())
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

