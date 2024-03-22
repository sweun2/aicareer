package co.unlearning.aicareer.domain.job.education;

import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    public enum DEGREE {
        IRRELEVANCE, HIGH_SCHOOL, BACHELOR, MASTER, DOCTOR
    }
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
    @Column
    private DEGREE degree;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private UserInterest userInterest;

}