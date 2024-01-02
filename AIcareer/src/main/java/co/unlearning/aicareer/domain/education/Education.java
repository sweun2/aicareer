package co.unlearning.aicareer.domain.education;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
    @Column
    private DEGREE degree;
}