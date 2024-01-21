package co.unlearning.aicareer.domain.board;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.bookmark.Bookmark;
import co.unlearning.aicareer.domain.career.Career;
import co.unlearning.aicareer.domain.company.Company;
import co.unlearning.aicareer.domain.education.Education;
import co.unlearning.aicareer.domain.recruitment.RecruitmentAddress;
import co.unlearning.aicareer.domain.recruitment.RecruitmentDeadlineType;
import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recrutingjob.RecruitingJob;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(unique = true)
    private String uid;
    @CreationTimestamp
    @Column
    private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
    @Column
    private String title; //제목
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @OneToOne
    @Cascade(CascadeType.ALL)
    private Image bannerImage;
    @Column
    private String pageLink;
}
