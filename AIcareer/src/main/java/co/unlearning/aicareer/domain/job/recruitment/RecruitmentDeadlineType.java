package co.unlearning.aicareer.domain.job.recruitment;

import lombok.Getter;

@Getter
public enum RecruitmentDeadlineType {
    ALL_TIME("상시 채용"),
    CLOSE_WHEN_RECRUITMENT("채용시 마감"),
    DUE_DATE("기한 설정"),
    EXPIRED("만료");

    private final String koreanName;

    RecruitmentDeadlineType(String koreanName) {
        this.koreanName = koreanName;
    }

}

