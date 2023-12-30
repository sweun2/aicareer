package co.unlearning.aicareer.domain.recruitmenttype;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private Recruitment recruitment;
    @Column
    private RecruitmentTypeName recruitmentTypeName;
}