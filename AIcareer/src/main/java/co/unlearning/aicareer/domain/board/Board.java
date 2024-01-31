package co.unlearning.aicareer.domain.board;

import co.unlearning.aicareer.domain.Image.Image;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime lastModified;
    @Column
    private String title; //제목
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @OneToOne
    @Cascade(CascadeType.ALL)
    private Image bannerImage;
    @Column
    private String pageLinkUrl;
    @Column
    private Boolean isView;
    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER, cascade = {jakarta.persistence.CascadeType.ALL}, orphanRemoval = true)
    private Set<Image> subImageSet;
}
