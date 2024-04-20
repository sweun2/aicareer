package co.unlearning.aicareer.domain.job.recrutingjob;

import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecruitingJob {
    @Getter
    public enum RecruitingJobName {
        MACHINE_LEARNING_ENGINEER("머신러닝 엔지니어"),
        DATA_SCIENTIST("데이터 사이언티스트"),
        DATA_ANALYST("데이터 분석가"),
        DATA_ENGINEER("데이터 엔지니어"),
        NLP("NLP"),
        RESEARCH("리서치"),
        COMPUTER_VISION("컴퓨터 비전"),
        GENERATIVE_AI("생성형 AI"),
        ETC("기타"),
        PM_PO("PM/PO"),
        CONSULTANT("컨설턴트"),
        SOFTWARE_ENGINEER("소프트웨어 엔지니어")
        ;
        private final String koreanName;
        RecruitingJobName(String koreanName) {
            this.koreanName = koreanName;
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
    @Column
    private RecruitingJobName recruitJobName;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private UserInterest userInterest;
}
