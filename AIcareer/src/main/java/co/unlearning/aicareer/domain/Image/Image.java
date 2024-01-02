package co.unlearning.aicareer.domain.Image;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String imageUrl;
    @Column
    private String imagePath;
    @Column
    private String originImageName;

    @CreationTimestamp
    @Column
    private LocalDateTime createdDate = LocalDateTime.now();
}
