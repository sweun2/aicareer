package co.unlearning.aicareer.domain.job.board;

import co.unlearning.aicareer.domain.job.boardimage.BoardImage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String uid;
    @CreationTimestamp
    @Column
    private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
    @Column
    private LocalDateTime lastModified;
    @Column
    private String title; //제목
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @Column
    private String pageLinkUrl;
    @Column
    private Boolean isView;
    @OneToOne(cascade = {CascadeType.ALL})
    private BoardImage desktopBannerImage;
    @OneToOne(cascade = {CascadeType.ALL})
    private BoardImage mobileBannerImage;

    @OneToMany(mappedBy = "board" ,fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<BoardImage> subImages;

}
