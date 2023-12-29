package co.unlearning.aicareer.domain.recruitment.service;

import co.unlearning.aicareer.domain.careerrequirement.dto.CareerRequirementResponseDto;
import co.unlearning.aicareer.domain.company.repository.CompanyRepository;
import co.unlearning.aicareer.domain.education.dto.EducationResponseDto;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.recrutingjob.dto.RecruitingJobResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final CompanyRepository companyRepository;
    public Recruitment findRecruitmentInfo(String uid) {
        return recruitmentRepository.findByUid(Long.valueOf(uid)).orElseThrow(
                () -> new ResponseStatusException(HttpStatusCode.valueOf(404),"text")
        );
    }
    /*public Recruitment findAllRecruitmentInfo() {
        return recruitmentRepository.findByUid().orElseThrow(
                () -> new ResponseStatusException(HttpStatusCode.valueOf(404),"text")
        );
    }*/
    public Recruitment addRecruitmentPost(RecruitmentRequirementDto.RecruitmentPost recruitmentPost) {
        //image 처리 필요


        /*private String companyAddress;
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
        private Integer hits; //조회수*/
        return new Recruitment();
    }
}
