package co.unlearning.aicareer.domain.common.user;

import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentAddress;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToMany(mappedBy = "userInterest",fetch = FetchType.EAGER, cascade = {CascadeType.ALL},orphanRemoval = true)
    private Set<Education> educationSet;
    @OneToMany(mappedBy = "userInterest",fetch = FetchType.EAGER, cascade = {CascadeType.ALL},orphanRemoval = true)
    private Set<RecruitingJob> recruitingJobSet;
    @OneToMany(mappedBy = "userInterest",fetch = FetchType.EAGER, cascade = {CascadeType.ALL},orphanRemoval = true)
    private Set<CompanyType> companyTypeSet;
    @Column
    private Boolean isMetropolitanArea;
    @OneToOne
    private User user;
    @Column
    private String receiveEmail;
}
