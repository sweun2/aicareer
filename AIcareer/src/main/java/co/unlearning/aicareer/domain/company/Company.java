package co.unlearning.aicareer.domain.company;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String companyName; //회사명
    @ManyToOne
    private Recruitment recruitment;
}
