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
    public enum RecruitingJobName {
        MACHINE_LEARNING_ENGINEER, DATA_SCIENTIST,DATA_ANALYST,DATA_ENGINEER,NLP,RESEARCH,COMPUTER_VISION, GENERATIVE_AI,ETC
        ,PRODUCT_MANAGER ,PRODUCT_OWNER
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
