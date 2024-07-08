package co.unlearning.aicareer.domain.common.seo.sitemap;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SiteMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String uid;
    @Column
    private String url;
    @Column
    private LocalDateTime lastModified;
}
