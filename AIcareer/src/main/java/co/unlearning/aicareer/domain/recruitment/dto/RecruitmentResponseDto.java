package co.unlearning.aicareer.domain.recruitment.dto;

import co.unlearning.aicareer.domain.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.domain.career.dto.CareerResponseDto;
import co.unlearning.aicareer.domain.company.dto.CompanyResponseDto;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitment.*;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter.LocalDateTimeToString;

public class RecruitmentResponseDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitmentInfo {
        //@Schema(description = "채용 공고 uid")
        private String recruitmentUid;
        //@Schema(description = "메인 이미지 url")
        private ImageResponseDto.ImageData mainImageUrl;
        //new table
        //@Schema(description = "회사 정보")
        private CompanyResponseDto.CompanyResponseInfo companyResponseInfo;
        //@Schema(description = "모집 직무",allowableValues = {})
        private List<RecruitingJobResponseDto.RecruitingJobNameDto> recruitingJobNames;
        //@Schema(description = "모집 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNameDto> recruitmentTypeNames;
        //@Schema(description = "최종 학력",allowableValues = {})
        private List<EducationResponseDto.EducationDto> educationDtos;
        //@Schema(description = "요구 경력",allowableValues = {})
        private List<CareerResponseDto.CareerDto> careerDtos;
        //@Schema(description = "모집 시작일")
        private LocalDateTime recruitmentStartDate; // 모집 시작일
        //@Schema(description = "모집 마감")
        private RecruitmentResponseDto.RecruitmentDeadLine recruitmentDeadLine;
/*
        private LocalDateTime recruitmentDeadline; //모집 마감일*/
        //@Schema(description = "업로드 날짜")
        private LocalDateTime uploadDate; //업로드 날짜
        //@Schema(description = "모집 공고 링크")
        private String recruitmentAnnouncementLink; //모집 공고 링크
        //@Schema(description = "조회수")
        private Integer hits; //조회수
        //@Schema(description = "지역")
        private String recruitmentAddress; //지역
        //@Schema(description = "제목")
        private String title; //title
        //@Schema(description = "내용")
        private String content; //내용

        public static RecruitmentInfo of(Recruitment recruitment) {
            return RecruitmentInfo.builder()
                    .recruitmentUid(String.valueOf(recruitment.getUid()))
                    .mainImageUrl(ImageResponseDto.ImageData.of(recruitment.getMainImage()))
                    .companyResponseInfo(CompanyResponseDto.CompanyResponseInfo.of(recruitment.getCompany()))
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNameDto.of(List.copyOf(recruitment.getRecruitingJobSet())))
                    .recruitmentTypeNames(RecruitmentTypeResponseDto.RecruitmentTypeNameDto.of(List.copyOf(recruitment.getRecruitmentTypeSet())))
                    .educationDtos(EducationResponseDto.EducationDto.of(List.copyOf(recruitment.getEducationSet())))
                    .careerDtos(CareerResponseDto.CareerDto.of(List.copyOf(recruitment.getCareerSet())))
                    .recruitmentStartDate(recruitment.getRecruitmentStartDate())
                    .recruitmentDeadLine(RecruitmentDeadLine.of(recruitment.getRecruitmentDeadlineType(),LocalDateTimeToString(recruitment.getRecruitmentDeadline())))
                    .uploadDate(recruitment.getUploadDate())
                    .recruitmentAnnouncementLink(recruitment.getRecruitmentAnnouncementLink())
                    .hits(recruitment.getHits())
                    .recruitmentAddress(recruitment.getRecruitmentAddress())
                    .title(recruitment.getTitle())
                    .content(recruitment.getContent())
                    .build();
        }
        public static List<RecruitmentInfo> of(List<Recruitment> companies) {
            return companies.stream().map(RecruitmentInfo::of).collect(Collectors.toList());
        }
    }
    @Getter
    @Setter
    @Builder
    public static class RecruitmentSimple {
        //@Schema(description = "채용 공고 uid")
        private String recruitmentUid;
        //@Schema(description = "메인 이미지 url")
        private String mainImageUrl;
        //new table
        //@Schema(description = "회사 정보")
        private CompanyResponseDto.CompanyResponseInfo companyResponseInfo;
        //@Schema(description = "모집 직무",allowableValues = {"RESEARCH", "MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST", "ETC"})
        private List<RecruitingJobResponseDto.RecruitingJobNameDto> recruitingJobNames;
        //@Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<RecruitmentTypeResponseDto.RecruitmentTypeNameDto> recruitmentTypeNames;
        //@Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR"})
        private List<EducationResponseDto.EducationDto> educations;
        //@Schema(description = "경력 조건",allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER"})
        private List<CareerResponseDto.CareerDto> careers;
        //@Schema(description = "모집 시작일")
        private String recruitmentStartDate; // 모집 시작일

        /*@Schema(description = "채용 공고 마감 종류", allowableValues = {"ALL_TIME", "CLOSE_WHEN_RECRUITMENT", "DUE_DATE"})
        private String deadlineTYPE;
        @Schema(description = "모집 마감일/ deadlineFormat이 DUE_DATE인 경우에만 입력")
        private String recruitmentDeadline; //모집 마감일*/
        //@Schema(description = "마감 유형/날짜 ")
        private RecruitmentDeadLine recruitmentDeadLine;
        //@Schema(description = "업로드 날짜")
        private String uploadDate; //업로드 날짜
        //@Schema(description = "지역")
        private String recruitmentAddress; //지역
        //@Schema(description = "제목")
        private String title; //title
        //@Schema(description = "조회수")
        private Integer hits; //조회수

        public static RecruitmentSimple of(Recruitment recruitment) {
            return RecruitmentSimple.builder()
                    .recruitmentUid(String.valueOf(recruitment.getUid()))
                    .mainImageUrl(recruitment.getMainImage().getImageUrl())
                    .companyResponseInfo(CompanyResponseDto.CompanyResponseInfo.of(recruitment.getCompany()))
                    .recruitingJobNames(RecruitingJobResponseDto.RecruitingJobNameDto.of(List.copyOf(recruitment.getRecruitingJobSet())))
                    .recruitmentTypeNames(RecruitmentTypeResponseDto.RecruitmentTypeNameDto.of(List.copyOf(recruitment.getRecruitmentTypeSet())))
                    .educations(EducationResponseDto.EducationDto.of(List.copyOf(recruitment.getEducationSet())))
                    .careers(CareerResponseDto.CareerDto.of(List.copyOf(recruitment.getCareerSet())))
                    .recruitmentStartDate(LocalDateTimeToString(recruitment.getRecruitmentStartDate()))
                    .recruitmentDeadLine(RecruitmentDeadLine.of(recruitment.getRecruitmentDeadlineType(),LocalDateTimeToString(recruitment.getRecruitmentDeadline())))
                    .uploadDate(LocalDateTimeToString(recruitment.getUploadDate()))
                    .recruitmentAddress(recruitment.getRecruitmentAddress())
                    .title(recruitment.getTitle())
                    .hits(recruitment.getHits())
                    .build();
        }
        public static List<RecruitmentSimple> of(List<Recruitment> companies) {
            return companies.stream().map(RecruitmentSimple::of).collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @Builder
    public static class RecruitmentDeadLine {
        //@Schema(description = "채용 공고 마감 종류", allowableValues = {"ALL_TIME", "CLOSE_WHEN_RECRUITMENT", "DUE_DATE"})
        private String deadlineType;
        //@Schema(description = "모집 마감일/ deadlineType이 DUE_DATE인 경우에만 입력")
        private String recruitmentDeadline; //모집 마감일
        public static RecruitmentDeadLine of(RecruitmentDeadlineType deadlineType, String recruitmentDeadline) {
            return RecruitmentDeadLine.builder().deadlineType(deadlineType.toString()).recruitmentDeadline(recruitmentDeadline).build();
        }
    }
}
