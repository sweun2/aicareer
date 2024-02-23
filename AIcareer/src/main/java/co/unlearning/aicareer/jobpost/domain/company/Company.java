package co.unlearning.aicareer.jobpost.domain.company;

import co.unlearning.aicareer.jobpost.domain.companytype.CompanyType;
import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
    private String uid;
    @Column
    private String companyAddress; //회사 주소
    @Column
    private String companyName; //회사명
    @OneToOne
    @Cascade(CascadeType.ALL)
    private CompanyType companyType; //회사 타입
    @OneToMany(mappedBy = "company")
    private Set<Recruitment> recruitmentSet;
}
