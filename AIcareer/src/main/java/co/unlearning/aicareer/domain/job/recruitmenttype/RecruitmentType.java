package co.unlearning.aicareer.domain.job.recruitmenttype;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentType {
    public enum RecruitmentTypeName {
        INTERN ,FULL_TIME,CONTRACT,INDUSTRIAL_TECHNICAL,PROFESSIONAL_RESEARCH
    }
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
    @Column
    private RecruitmentTypeName recruitmentTypeName;
}