package co.unlearning.aicareer.domain.job.recruitment;

import co.unlearning.aicareer.domain.job.bookmark.Bookmark;
import co.unlearning.aicareer.domain.job.career.Career;
import co.unlearning.aicareer.domain.job.company.Company;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recruitmentImage.RecruitmentImage;
import co.unlearning.aicareer.domain.job.recruitmentbatch.RecruitmentBatch;
import co.unlearning.aicareer.domain.job.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String uid; // 채용 공고 uid
    @ManyToOne
    private Company company;
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<RecruitingJob> recruitingJobSet;
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<RecruitmentType> recruitmentTypeSet; //채용 유형 -> new table
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<Education> educationSet; //최종 학력
    @OneToMany(mappedBy = "recruitment",fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
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
    private Set<Bookmark> bookmarkSet;
    @OneToOne(cascade = CascadeType.ALL)
    private RecruitmentImage mainImage;
    @OneToMany(mappedBy ="recruitment",fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<RecruitmentImage> subImages;
    @Column
    private LocalDateTime lastModified;
    @OneToOne(cascade = {CascadeType.REMOVE},orphanRemoval = true)
    private RecruitmentBatch recruitmentBatch;

}