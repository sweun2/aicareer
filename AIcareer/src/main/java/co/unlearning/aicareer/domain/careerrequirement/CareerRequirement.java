package co.unlearning.aicareer.domain.careerrequirement;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import jakarta.persistence.*;


@Entity
public class CareerRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private Recruitment recruitment;
    @Column
    private String requirement;
}