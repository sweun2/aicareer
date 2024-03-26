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
    @Getter
    public enum DEGREE {
        IRRELEVANCE("무관"),
        HIGH_SCHOOL("고졸"),
        BACHELOR("학사"),
        MASTER("석사"),
        DOCTOR("박사");

        private final String koreanName;

        DEGREE(String koreanName) {
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
    private DEGREE degree;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private UserInterest userInterest;

}