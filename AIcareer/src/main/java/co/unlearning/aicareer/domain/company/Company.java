package co.unlearning.aicareer.domain.company;

import co.unlearning.aicareer.domain.recruitment.CompanyType;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
    private Long uid;
    @Column
    private String companyAddress; //회사 주소
    @Column
    private String companyName; //회사명
    @Column
    private CompanyType companyType; //회사 타입
    @OneToMany
    private Set<Recruitment> recruitments;
}
