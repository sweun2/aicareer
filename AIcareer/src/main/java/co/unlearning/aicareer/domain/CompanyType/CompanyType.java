package co.unlearning.aicareer.domain.CompanyType;

import co.unlearning.aicareer.domain.company.Company;
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
        STARTUP, MAJOR, UNICORN, MIDDLE_MARKET;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne
    @Cascade(CascadeType.ALL)
    private Company company;
    @Column
    private CompanyTypeName companyTypeName;
}