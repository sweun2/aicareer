package co.unlearning.aicareer.domain.common.user.dto;

import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;
import java.util.Set;

public class UserRequestDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginForm {
        @NotEmpty
        private String email;
        @NotEmpty
        private String password;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRole {
        @NotEmpty
        private String userRole;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTermsInfo {
        @Schema(description = "마케팅 동의 여부")
        private Boolean isMarketing;
        @Schema(description = "정보성 알람 동의 여부")
        private Boolean isInformationTerms;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInterestInfo {
        @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC", "ETC"})
        private List<String> companyTypes;
        @Schema(description = "모집 직무",allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC","PRODUCT_MANAGER","PRODUCT_OWNER"})
        private List<String> recruitingJobNames;
        @Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR"})
        private List<String> educations;
        @Schema(description = "수도권 여부",allowableValues = {"true,false"})
        private Boolean isMetropolitanArea;
        @Schema(description = "알람 받을 이메일")
        private String receivedEmail;
    }
}
