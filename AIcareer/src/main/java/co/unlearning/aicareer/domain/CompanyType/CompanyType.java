package co.unlearning.aicareer.domain.CompanyType;

import co.unlearning.aicareer.domain.company.Company;
import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne
    private Company company;
    @Column
    private CompanyTypeName companyTypeName;
}