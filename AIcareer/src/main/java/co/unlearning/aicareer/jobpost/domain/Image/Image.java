package co.unlearning.aicareer.jobpost.domain.Image;

import co.unlearning.aicareer.jobpost.domain.board.Board;
import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
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
    @Column(unique = true)
    private String imageUrl;
    @CreationTimestamp
    @Column
    private LocalDateTime createdDate = LocalDateTime.now();
    @ManyToOne
    private Recruitment recruitment;
    @ManyToOne
    private Board board;
}
