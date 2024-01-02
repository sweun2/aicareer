package co.unlearning.aicareer.domain.recrutingjob;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecruitingJob {
    public enum RecruitingJobName {
        RESEARCH, MACHINE_LEARNING_ENGINEER, DATA_SCIENTIST, ETC
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private Recruitment recruitment;
    @Column
    private RecruitingJobName recruitJobName;
}
