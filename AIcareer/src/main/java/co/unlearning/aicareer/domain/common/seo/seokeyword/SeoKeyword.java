package co.unlearning.aicareer.domain.common.seo.seokeyword;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SeoKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String keyword;
    @ManyToOne
    private Recruitment recruitment;
}