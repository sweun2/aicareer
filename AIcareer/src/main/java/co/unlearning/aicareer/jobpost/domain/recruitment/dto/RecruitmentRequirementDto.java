package co.unlearning.aicareer.jobpost.domain.recruitment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
public class RecruitmentRequirementDto {
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class RecruitmentPost {
        @Schema(description = "메인 이미지 url")
        private String mainImage;
        @Schema(description = "서브 이미지 url")
        private List<String> subImage;
        @Schema(description = "회사 주소")
        private String companyAddress;
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC","FOREIGN","ETC"})
        private String companyType;
        @Schema(description = "모집 직무",allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC"})
        @NotNull
        private List<String> recruitingJobNames;
        @Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        @NotNull
        private List<String> recruitmentTypeNames;
        @Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR", "IRRELEVANCE"})
        @NotNull
        private List<String> educations;
        @Schema(description = "요구 경력", allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER","IRRELEVANCE"})
        private List<String> careers;
        @Schema(description = "모집 시작일, 일자/시간 사이 빈칸 필요", defaultValue = "작성 시 시간", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
        private String recruitmentStartDate; // 모집 시작일
        @Schema(description = "모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
        private RecruitmentDeadLine recruitmentDeadline; //모집 마감일
        @Schema(description = "모집 공고 링크")
        private String recruitmentAnnouncementLink; //모집 공고 링크
        @Schema(description = "모집 지역",allowableValues = {"SEOUL", "GANGNAM","MAPO","GURO_GARSAN_GAME","BUNDANG_PANGYO","ETC"})
        private String recruitmentAddress; //지역
        @Schema(description = "제목")
        private String title; //title
        @Schema(description = "내용")
        private String content; //내용
    }
    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class RecruitmentDeadLine {
        @Schema(description = "채용 공고 마감 종류", allowableValues = {"ALL_TIME", "CLOSE_WHEN_RECRUITMENT", "DUE_DATE"})
        private String deadlineType;
        @Schema(description = "모집 마감일/ deadlineType이 DUE_DATE인 경우에만 입력, 모집 마감일 일자/시간 사이 빈칸 필요", allowableValues = {"yyyy-MM-dd HH:mm","2024-01-02 13:45"})
        private String recruitmentDeadline; //모집 마감일
    }

    @Getter
    @Setter
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class Search {
        @Schema(description = "마감 여부, true 시 마감된 공고가 제외", allowableValues = {"true,false"})
        private String isOpen; // 마감 여부
        @Schema(description = "정렬 기준, 인기 순/마감임박 순/업로드 순 ", allowableValues = {"HITS","DEADLINE","UPLOAD"})
        private String sortCondition;
        @Schema(description = "정렬 순서, 내림차 순/오름차 순", allowableValues = {"DESC","ASC"})
        private String orderBy;
        @Schema(description = "회사 타입", allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC", "ETC"})
        private List<String> companyTypes;
        @Schema(description = "모집 직무",allowableValues = {"MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST","DATA_ANALYST","DATA_ENGINEER","NLP","RESEARCH","COMPUTER_VISION", "GENERATIVE_AI","ETC"})
        private List<String> recruitingJobNames;
        @Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<String> recruitmentTypeNames;
        @Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR"})
        private List<String> educations;
        @Schema(description = "요구 경력", allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER"})
        private List<String> careers;
        @Schema(description = "모집 지역",allowableValues = {"SEOUL", "GANGNAM","MAPO","GURO_GARSAN_GAME","BUNDANG_PANGYO","ETC"})
        private List<String> recruitmentAddress; //지역
    }


}