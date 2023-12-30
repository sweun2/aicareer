package co.unlearning.aicareer.domain.recruitment.dto;

import co.unlearning.aicareer.domain.careerrequirement.dto.CareerRequirementResponseDto;
import co.unlearning.aicareer.domain.company.dto.CompanyResponseDto;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitment.*;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RecruitmentResponseDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitmentInfo {
        private String image;
        //new table

        @Schema(description = "회사 정보")
        private CompanyResponseDto.CompanyInfo companyInfo;
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

        public static RecruitmentInfo of(Recruitment recruitment) {
            return RecruitmentInfo.builder()
                    .companyInfo(CompanyResponseDto.CompanyInfo.of(recruitment.getCompany()))
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNames.of(List.copyOf(recruitment.getRecruitingJobSet())))
                    .recruitmentTypeNames(RecruitmentTypeResponseDto.RecruitmentTypeNames.of(List.copyOf(recruitment.getRecruitmentTypeSet())))
                    .educationRequirements(EducationResponseDto.EducationRequirement.of(List.copyOf(recruitment.getEducationSet())))
                    .careerRequirements(CareerRequirementResponseDto.Career.of(List.copyOf(recruitment.getCareerRequirementSet())))
                    .recruitmentStartDate(recruitment.getRecruitmentStartDate())
                    .recruitmentDeadline(recruitment.getRecruitmentDeadline())
                    .uploadDate(recruitment.getUploadDate())
                    .recruitmentAnnouncementLink(recruitment.getRecruitmentAnnouncementLink())
                    .hits(recruitment.getHits())
                    .build();
        }
        public static List<RecruitmentInfo> of(List<Recruitment> companies) {
            return companies.stream().map(RecruitmentInfo::of).collect(Collectors.toList());
        }
    }
}
