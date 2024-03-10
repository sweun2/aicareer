package co.unlearning.aicareer.domain.job.companytype;

import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.job.company.Company;
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
public class CompanyType {
    public enum CompanyTypeName {
        STARTUP, MAJOR, UNICORN, MIDDLE_MARKET, PUBLIC, FOREIGN, ETC
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne
    @Cascade(CascadeType.ALL)
    private Company company;
    @Column
    private CompanyTypeName companyTypeName;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private UserInterest userInterest;
}