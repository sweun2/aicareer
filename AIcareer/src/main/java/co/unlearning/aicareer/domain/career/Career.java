package co.unlearning.aicareer.domain.career;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
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
    public enum AnnualLeave{
        NEW_COMER,JUNIOR,SENIOR,MIDDLE,LEADER
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
    @Column
    private AnnualLeave annualLeave;
}