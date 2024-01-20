package co.unlearning.aicareer.domain.recruitment;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.bookmark.Bookmark;
import co.unlearning.aicareer.domain.career.Career;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
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
    private String uid; // 채용 공고 uid
    @ManyToOne
    private Company company;
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private Set<RecruitingJob> recruitingJobSet;
    @Cascade(CascadeType.ALL)
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER)
    private Set<RecruitmentType> recruitmentTypeSet; //채용 유형 -> new table
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private Set<Education> educationSet; //최종 학력
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private Set<Career> careerSet; // 요구 경력
    @Column
    private LocalDateTime recruitmentStartDate; // 모집 시작일
    @Column
    private RecruitmentDeadlineType recruitmentDeadlineType; //모집 마감 유형
    @Column
    private LocalDateTime recruitmentDeadline; //모집 마감일
    @CreationTimestamp
    @Column
    private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
    @Column
    private String title; //제목
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @Column
    private String recruitmentAnnouncementLink; //모집 공고 링크
    @Column
    private Integer hits; //조회수
    @Column
    private RecruitmentAddress recruitmentAddress; //지역
    @OneToMany(mappedBy = "recruitment")
    @Cascade(CascadeType.REMOVE)
    private Set<Bookmark> bookmarkSet;
    @OneToOne
    @Cascade(CascadeType.ALL)
    private Image mainImage;
}

