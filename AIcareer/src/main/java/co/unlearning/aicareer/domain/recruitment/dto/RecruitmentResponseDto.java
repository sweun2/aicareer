package co.unlearning.aicareer.domain.recruitment.dto;

import co.unlearning.aicareer.domain.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.domain.career.dto.CareerResponseDto;
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
        @Schema(description = "채용 공고 uid")
        private String recruitmentUid;
        @Schema(description = "메인 이미지 url")
        private ImageResponseDto.ImageData mainImage;
        @Schema(description = "내용 이미지 url 리스트")
        private List<ImageResponseDto.ImageData> contentImage;
        //new table
        @Schema(description = "회사 정보")
        private CompanyResponseDto.CompanyInfo companyInfo;
        @Schema(description = "모집 직무",allowableValues = {})
        private List<RecruitingJobResponseDto.RecruitingJobNameDto> recruitingJobNames;
        @Schema(description = "모집 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNameDto> recruitmentTypeNames;
        @Schema(description = "최종 학력",allowableValues = {})
        private List<EducationResponseDto.EducationDto> educationDtos;
        @Schema(description = "요구 경력",allowableValues = {})
        private List<CareerResponseDto.CareerDto> careerDtos;
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
                    .recruitmentUid(String.valueOf(recruitment.getUid()))
                    .mainImage(ImageResponseDto.ImageData.of(recruitment.getMainImage()))
                    .contentImage(ImageResponseDto.ImageData.of(List.copyOf(recruitment.getContentImage())))
                    .companyInfo(CompanyResponseDto.CompanyInfo.of(recruitment.getCompany()))
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNameDto.of(List.copyOf(recruitment.getRecruitingJobSet())))
                    .recruitmentTypeNames(RecruitmentTypeResponseDto.RecruitmentTypeNameDto.of(List.copyOf(recruitment.getRecruitmentTypeSet())))
                    .educationDtos(EducationResponseDto.EducationDto.of(List.copyOf(recruitment.getEducationSet())))
                    .careerDtos(CareerResponseDto.CareerDto.of(List.copyOf(recruitment.getCareerSet())))
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
        @Schema(description = "채용 공고 uid")
        private String recruitmentUid;
        @Schema(description = "메인 이미지 url")
        private String mainImageUrl;
        //new table
        @Schema(description = "회사 정보")
        private CompanyResponseDto.CompanyInfo companyInfo;
        @Schema(description = "모집 직무",allowableValues = {})
        private List<RecruitingJobResponseDto.RecruitingJobNameDto> recruitingJobNames;
        @Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNameDto> recruitmentTypeNames;
        @Schema(description = "학력 조건",allowableValues = {})
        private List<EducationResponseDto.EducationDto> educationDtos;
        @Schema(description = "경력 조건",allowableValues = {})
        private List<CareerResponseDto.CareerDto> careerDtoRequirements;
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
                    .recruitmentUid(String.valueOf(recruitment.getUid()))
                    .mainImageUrl(recruitment.getMainImage().getUrl())
                    .companyInfo(CompanyResponseDto.CompanyInfo.of(recruitment.getCompany()))
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNameDto.of(List.copyOf(recruitment.getRecruitingJobSet())))
                    .recruitmentTypeNames(RecruitmentTypeResponseDto.RecruitmentTypeNameDto.of(List.copyOf(recruitment.getRecruitmentTypeSet())))
                    .educationDtos(EducationResponseDto.EducationDto.of(List.copyOf(recruitment.getEducationSet())))
                    .careerDtoRequirements(CareerResponseDto.CareerDto.of(List.copyOf(recruitment.getCareerSet())))
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
