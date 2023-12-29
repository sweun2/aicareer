package co.unlearning.aicareer.domain.recruitment.dto;

import co.unlearning.aicareer.domain.careerrequirement.dto.CareerRequirementResponseDto;
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
        private String image;
        @Schema(description = "회사 주소")
        private String companyAddress;
        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE"})
        private String companyType;
        @Schema(description = "모집 직무",allowableValues = {})
        private List<RecruitingJobResponseDto.RecruitingJobNames> recruitingJobNames;
        @Schema(description = "모집 유형",allowableValues = {})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNames> recruitmentTypeNames;
        @Schema(description = "최종 학력",allowableValues = {})
        private List<EducationResponseDto.EducationRequirement> educationRequirements;
        @Schema(description = "요구 경력",allowableValues = {})
        private List<CareerRequirementResponseDto.Career> careerRequirements;
        @Schema(description = "모집 시작일",allowableValues = {})
        private LocalDateTime recruitmentStartDate; // 모집 시작일
        @Schema(description = "모집 마감일",allowableValues = {})
        private LocalDateTime recruitmentDeadline; //모집 마감일
        @Schema(description = "업로드 날짜",allowableValues = {})
        private LocalDateTime uploadDate; //업로드 날짜
        @Schema(description = "업로드 링크",allowableValues = {})
        private String recruitmentAnnouncementLink; //모집 공고 링크
        @Schema(description = "조회수",allowableValues = {})
        private Integer hits; //조회수
    }
}
