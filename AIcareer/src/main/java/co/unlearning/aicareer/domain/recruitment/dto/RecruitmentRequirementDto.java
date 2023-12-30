package co.unlearning.aicareer.domain.recruitment.dto;

import co.unlearning.aicareer.domain.careerrequirement.CareerRequirement;
import co.unlearning.aicareer.domain.careerrequirement.dto.CareerRequirementResponseDto;
import co.unlearning.aicareer.domain.company.CompanyType;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class RecruitmentRequirementDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitmentPost {
        @Schema(description = "회사 주소")
        private String companyAddress;
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE"})
        private String companyType;
        @Schema(description = "모집 직무", allowableValues = {})
        private List<RecruitingJobResponseDto.RecruitingJobNames> recruitingJobNames;
        @Schema(description = "모집 유형", allowableValues = {})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNames> recruitmentTypeNames;
        @Schema(description = "최종 학력", allowableValues = {})
        private List<EducationResponseDto.EducationRequirement> educationRequirements;
        @Schema(description = "요구 경력", allowableValues = {})
        private List<CareerRequirementResponseDto.Career> careerRequirements;
        @Schema(description = "모집 시작일", defaultValue = "작성 시 시간", allowableValues = {})
        private LocalDateTime recruitmentStartDate; // 모집 시작일
        @Schema(description = "모집 마감일", allowableValues = {})
        private LocalDateTime recruitmentDeadline; //모집 마감일
        @Schema(description = "모집 공고 링크", allowableValues = {})
        private String recruitmentAnnouncementLink; //모집 공고 링크
        @Schema(description = "모집 지역", allowableValues = {})
        private String recruitmentAddress; //지역
        @Schema(description = "내용", allowableValues = {})
        private String content; //내용

    }

    @Getter
    @Setter
    @Builder
    public static class Search {
        @Schema(description = "모집 직무", allowableValues = {})
        private List<String> recruitingJobs; //하는 업무
        @Schema(description = "채용 유형", allowableValues = {})
        private List<String> recruitmentTypes; //채용 유형
        @Schema(description = "회사 유형", allowableValues = {})
        private List<String> companyTypes; //회사 종류
        @Schema(description = "학력 조건", allowableValues = {})
        private List<String> educationRequirements; //학력 조건
        @Schema(description = "경력 조건", allowableValues = {})
        private List<String> careerRequirements; //경력 조건
        @Schema(description = "지역", allowableValues = {})
        private List<String> recruitmentAddress; //지역
        @Schema(description = "마감 여부, true 시 마감된 공고가 제외", allowableValues = {})
        private Boolean IsOpen; // 마감 여부
        @Schema(description = "정렬 기준, 인기순/마감 임박순/업로드 순 ", allowableValues = {"HITS","DEADLINE","UPLOAD"})
        private String sortCondition;
        @Schema(description = "정렬 순서, 내림차 순/오름차 순", allowableValues = {"DESC","ASC"})
        private String orderBy;
    }
}