package co.unlearning.aicareer.domain.recrutingjob;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
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
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
    @Column
    private RecruitingJobName recruitJobName;
}
