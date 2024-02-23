package co.unlearning.aicareer.jobpost.domain.sitemap;

import co.unlearning.aicareer.jobpost.domain.board.Board;
import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SiteMap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String uid;
    @Column
    private String url;
    @Column
    private LocalDateTime lastModified;
}
