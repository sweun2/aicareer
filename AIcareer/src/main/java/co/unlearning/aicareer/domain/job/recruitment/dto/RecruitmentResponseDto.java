package co.unlearning.aicareer.domain.job.recruitment.dto;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.job.recruitmentImage.RecruitmentImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter.LocalDateTimeToString;

public class RecruitmentResponseDto {
    @Getter
    @Setter
    @Builder
    public static class RecruitmentInfo {
        private String recruitmentUid;
        private String mainImageUrl;
        private List<String> subImageUrls;

        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC", "ETC"})
        private String companyTypeInfo;
        @Schema(description = "회사 주소")
        private String companyAddress;

        private List<String> recruitingJobNames;
        private List<String> recruitmentTypeNames;
        private List<String> educations;
        private List<String> careers;

        private String recruitmentStartDate; // 모집 시작일
        //@Schema(description = "모집 마감")
        private String deadlineType;
        //@Schema(description = "모집 마감일/ deadlineType이 DUE_DATE인 경우에만 입력")
        private String recruitmentDeadline; //모집 마감일
        private String uploadDate; //업로드 날짜
        private String lastModified; // 최종 변경일
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
            RecruitmentInfoBuilder builder =  RecruitmentInfo.builder()
                    .recruitmentUid(String.valueOf(recruitment.getUid()))
                    .companyName(recruitment.getCompany().getCompanyName())
                    .companyTypeInfo(String.valueOf(recruitment.getCompany().getCompanyType().getCompanyTypeName()))
                    .companyAddress(recruitment.getCompany().getCompanyAddress())
                    .recruitingJobNames(recruitment.getRecruitingJobSet().stream().map(recruitingJob -> String.valueOf(recruitingJob.getRecruitJobName())).collect(Collectors.toList()))
                    .recruitmentTypeNames(recruitment.getRecruitmentTypeSet().stream().map(recruitmentType -> String.valueOf(recruitmentType.getRecruitmentTypeName())).collect(Collectors.toList()))
                    .educations(recruitment.getEducationSet().stream().map(education -> String.valueOf(education.getDegree())).collect(Collectors.toList()))
                    .careers(recruitment.getCareerSet().stream().map(career -> String.valueOf(career.getAnnualLeave())).collect(Collectors.toList()))
                    .recruitmentStartDate(LocalDateTimeToString(recruitment.getRecruitmentStartDate()))
                    .deadlineType(recruitment.getRecruitmentDeadlineType().toString())
                    .recruitmentDeadline(LocalDateTimeToString(recruitment.getRecruitmentDeadline()))
                    .uploadDate(LocalDateTimeToString(recruitment.getUploadDate()))
                    .lastModified(LocalDateTimeToString(recruitment.getLastModified()))
                    .recruitmentAnnouncementLink(recruitment.getRecruitmentAnnouncementLink())
                    .hits(recruitment.getHits())
                    .recruitmentAddress(String.valueOf(recruitment.getRecruitmentAddress()))
                    .title(recruitment.getTitle())
                    .content(recruitment.getContent());

            if (recruitment.getMainImage() != null) {
                builder.mainImageUrl(recruitment.getMainImage().getImage().getImageUrl());
            } else builder.mainImageUrl(StringUtils.EMPTY);
            if(!recruitment.getSubImages().isEmpty()) {
                builder.subImageUrls(
                        recruitment.getSubImages().stream()
                                .filter(recruitmentImage -> recruitmentImage.getImageOrder() != null && recruitmentImage.getImageOrder() != 0)
                                .sorted(Comparator.comparingInt(RecruitmentImage::getImageOrder))
                                .map(recruitmentImage -> recruitmentImage.getImage().getImageUrl())
                                .collect(Collectors.toList())
                );
            } else builder.subImageUrls(new ArrayList<>());

            return builder.build();
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

        @Schema(description = "회사명")
        private String companyName;
        @Schema(description = "회사 타입",allowableValues = {"STARTUP", "MAJOR", "UNICORN", "MIDDLE_MARKET","PUBLIC", "ETC"})
        private String companyTypeInfo;
        @Schema(description = "회사 주소")
        private String companyAddress;
        //@Schema(description = "모집 직무",allowableValues = {"RESEARCH", "MACHINE_LEARNING_ENGINEER", "DATA_SCIENTIST", "ETC"})
        private List<String> recruitingJobNames;
        //@Schema(description = "채용 유형",allowableValues = {"INTERN" ,"FULL_TIME","CONTRACT","INDUSTRIAL_TECHNICAL","PROFESSIONAL_RESEARCH"})
        private List<String> recruitmentTypeNames;
        //@Schema(description = "학력 조건",allowableValues = {"IRRELEVANCE", "HIGH_SCHOOL", "BACHELOR", "MASTER", "DOCTOR"})
        private List<String> educations;
        //@Schema(description = "경력 조건",allowableValues = {"NEW_COMER","JUNIOR","SENIOR","MIDDLE","LEADER"})
        private List<String> careers;
        //@Schema(description = "모집 시작일")
        private String recruitmentStartDate; // 모집 시작일

        /*@Schema(description = "채용 공고 마감 종류", allowableValues = {"ALL_TIME", "CLOSE_WHEN_RECRUITMENT", "DUE_DATE"})
        private String deadlineTYPE;
        @Schema(description = "모집 마감일/ deadlineFormat이 DUE_DATE인 경우에만 입력")
        private String recruitmentDeadline; //모집 마감일*/
        //@Schema(description = "마감 유형/날짜 ")
        private String deadlineType;
        //@Schema(description = "모집 마감일/ deadlineType이 DUE_DATE인 경우에만 입력")
        private String recruitmentDeadline; //모집 마감일
        private String uploadDate; //업로드 날짜
        private String lastModified; // 최종 변경일
        //@Schema(description = "지역")
        private String recruitmentAddress; //지역
        //@Schema(description = "제목")
        private String title; //title
        //@Schema(description = "조회수")
        private Integer hits; //조회수


        public static RecruitmentSimple of(Recruitment recruitment) {
            RecruitmentSimpleBuilder builder = RecruitmentSimple.builder()
                    .recruitmentUid(String.valueOf(recruitment.getUid()))
                    .companyName(recruitment.getCompany().getCompanyName())
                    .companyTypeInfo(String.valueOf(recruitment.getCompany().getCompanyType().getCompanyTypeName()))
                    .companyAddress(recruitment.getCompany().getCompanyAddress())
                    .recruitingJobNames(recruitment.getRecruitingJobSet().stream().map(recruitingJob -> String.valueOf(recruitingJob.getRecruitJobName())).collect(Collectors.toList()))
                    .recruitmentTypeNames(recruitment.getRecruitmentTypeSet().stream().map(recruitmentType -> String.valueOf(recruitmentType.getRecruitmentTypeName())).collect(Collectors.toList()))
                    .educations(recruitment.getEducationSet().stream().map(education -> String.valueOf(education.getDegree())).collect(Collectors.toList()))
                    .careers(recruitment.getCareerSet().stream().map(career -> String.valueOf(career.getAnnualLeave())).collect(Collectors.toList()))
                    .recruitmentStartDate(LocalDateTimeToString(recruitment.getRecruitmentStartDate()))
                    .deadlineType(recruitment.getRecruitmentDeadlineType().toString())
                    .recruitmentDeadline(LocalDateTimeToString(recruitment.getRecruitmentDeadline()))
                    .uploadDate(LocalDateTimeToString(recruitment.getUploadDate()))
                    .lastModified(LocalDateTimeToString(recruitment.getLastModified()))
                    .hits(recruitment.getHits())
                    .recruitmentAddress(String.valueOf(recruitment.getRecruitmentAddress()))
                    .title(recruitment.getTitle());

            if (recruitment.getMainImage() != null) {
                builder.mainImageUrl(recruitment.getMainImage().getImage().getImageUrl());
            } else builder.mainImageUrl(StringUtils.EMPTY);
            return builder.build();
        }
        public static List<RecruitmentSimple> of(List<Recruitment> companies) {
            return companies.stream().map(RecruitmentSimple::of).collect(Collectors.toList());
        }
    }
}
