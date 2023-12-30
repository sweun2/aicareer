package co.unlearning.aicareer.domain.recruitment;

import co.unlearning.aicareer.domain.careerrequirement.CareerRequirement;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private Long uid; // 채용 공고 uid
    @ManyToOne
    private Company company;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<RecruitingJob> recruitingJobSet;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<RecruitmentType> recruitmentTypeSet; //채용 유형 -> new table
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Education> educationSet; //최종 학력
    @OneToMany(fetch = FetchType.EAGER)
    private Set<CareerRequirement> careerRequirementSet; // 요구 경력
    @Column
    private LocalDateTime recruitmentStartDate; // 모집 시작일
    @Column
    private LocalDateTime recruitmentDeadline; //모집 마감일
    @CreationTimestamp
    @Column
    private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @Column
    private String recruitmentAnnouncementLink; //모집 공고 링크
    @Column
    private Integer hits; //조회수
    @Column
    private String recruitmentAddress; //지역
    @ManyToOne
    private User user; //북마크
}

