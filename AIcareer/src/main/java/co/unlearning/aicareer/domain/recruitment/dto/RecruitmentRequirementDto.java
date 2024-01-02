package co.unlearning.aicareer.domain.recruitment.dto;

import co.unlearning.aicareer.domain.career.dto.CareerResponseDto;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class RecruitmentRequirementDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitmentPost {
        @Schema(description = "메인 이미지 url")
        private String mainImage;
        @Schema(description = "회사 주소")
        private String companyAddress;
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET"})
        private String companyType;
        @Schema(description = "모집 직무",allowableValues = {"RESEARCH", "MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST", "ETC"})
        private List<String> recruitingJobNames;
        @Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<String> recruitmentTypeNames;
        @Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR"})
        private List<String> educations;
        @Schema(description = "요구 경력", allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER"})
        private List<String> careers;
        @Schema(description = "모집 시작일, 일자/시간 사이 빈칸 필요", defaultValue = "작성 시 시간", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
        private String recruitmentStartDate; // 모집 시작일
        @Schema(description = "모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
        private String recruitmentDeadline; //모집 마감일
        @Schema(description = "모집 공고 링크")
        private String recruitmentAnnouncementLink; //모집 공고 링크
        @Schema(description = "모집 지역")
        private String recruitmentAddress; //지역
        @Schema(description = "내용")
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