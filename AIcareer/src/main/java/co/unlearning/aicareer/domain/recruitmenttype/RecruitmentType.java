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

    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private Recruitment recruitment;
    @Column
    private RecruitmentTypeName recruitmentTypeName;
}