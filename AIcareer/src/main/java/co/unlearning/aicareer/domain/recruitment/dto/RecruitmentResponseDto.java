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
    public static class Info {
        @Schema(description = "이미지 url")
        private List<String> images;
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
        @Schema(description = "모집 공고 링크",allowableValues = {})
        private String recruitmentAnnouncementLink; //모집 공고 링크
        @Schema(description = "조회수",allowableValues = {})
        private Integer hits; //조회수
        @Schema(description = "지역",allowableValues = {})
        private String recruitmentAddress; //지역
        @Schema(description = "내용",allowableValues = {})
        private String content; //내용

        public static Info of(Recruitment recruitment) {
            return Info.builder()
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
                    .recruitmentAddress(recruitment.getRecruitmentAddress())
                    .content(recruitment.getContent())
                    .build();
        }
        public static List<Info> of(List<Recruitment> companies) {
            return companies.stream().map(Info::of).collect(Collectors.toList());
        }
    }
    @Getter
    @Setter
    @Builder
    public static class Simple {
        @Schema(description = "이미지 url")
        private List<String> images;
        //new table
        @Schema(description = "회사 정보")
        private CompanyResponseDto.CompanyInfo companyInfo;
        @Schema(description = "모집 직무",allowableValues = {})
        private List<RecruitingJobResponseDto.RecruitingJobNames> recruitingJobNames;
        @Schema(description = "채용 유형",allowableValues = {})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNames> recruitmentTypeNames;
        @Schema(description = "학력 조건",allowableValues = {})
        private List<EducationResponseDto.EducationRequirement> educationRequirements;
        @Schema(description = "경력 조건",allowableValues = {})
        private List<CareerRequirementResponseDto.Career> careerRequirements;
        @Schema(description = "모집 시작일",allowableValues = {})
        private LocalDateTime recruitmentStartDate; // 모집 시작일
        @Schema(description = "모집 마감일",allowableValues = {})
        private LocalDateTime recruitmentDeadline; //모집 마감일
        @Schema(description = "업로드 날짜",allowableValues = {})
        private LocalDateTime uploadDate; //업로드 날짜
        @Schema(description = "지역",allowableValues = {})
        private String recruitmentAddress; //지역
        @Schema(description = "조회수",allowableValues = {})
        private Integer hits; //조회수

        public static Simple of(Recruitment recruitment) {
            return Simple.builder()
                    .companyInfo(CompanyResponseDto.CompanyInfo.of(recruitment.getCompany()))
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNames.of(List.copyOf(recruitment.getRecruitingJobSet())))
                    .recruitmentTypeNames(RecruitmentTypeResponseDto.RecruitmentTypeNames.of(List.copyOf(recruitment.getRecruitmentTypeSet())))
                    .educationRequirements(EducationResponseDto.EducationRequirement.of(List.copyOf(recruitment.getEducationSet())))
                    .careerRequirements(CareerRequirementResponseDto.Career.of(List.copyOf(recruitment.getCareerRequirementSet())))
                    .recruitmentStartDate(recruitment.getRecruitmentStartDate())
                    .recruitmentDeadline(recruitment.getRecruitmentDeadline())
                    .uploadDate(recruitment.getUploadDate())
                    .recruitmentAddress(recruitment.getRecruitmentAddress())
                    .hits(recruitment.getHits())
                    .build();
        }
        public static List<Simple> of(List<Recruitment> companies) {
            return companies.stream().map(Simple::of).collect(Collectors.toList());
        }
    }
}
