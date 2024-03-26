package co.unlearning.aicareer.domain.job.career;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Career {
    @Getter
    public enum AnnualLeave {
        NEW_COMER("신입"),
        JUNIOR("주니어"),
        SENIOR("시니어"),
        MIDDLE("중간"),
        LEADER("리더"),
        IRRELEVANCE("무관");

        private final String koreanName;

        AnnualLeave(String koreanName) {
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
    private AnnualLeave annualLeave;
}