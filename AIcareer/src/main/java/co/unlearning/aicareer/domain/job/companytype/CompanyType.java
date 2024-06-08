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
    @Getter
    public enum CompanyTypeName {
        STARTUP("스타트업"),
        MAJOR("대기업"),
        UNICORN("유니콘"),
        MIDDLE_MARKET("중견기업"),
        PUBLIC("공기업"),
        FOREIGN("외국계"),
        ETC("기타"),
        PUBLIC_INSTITUTION("공공기관");

        private final String koreanName;

        CompanyTypeName(String koreanName) {
            this.koreanName = koreanName;
        }

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private CompanyTypeName companyTypeName;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private UserInterest userInterest;
}