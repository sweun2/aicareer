package co.unlearning.aicareer.domain.job.boardimage;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.job.board.Board;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Board board;
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Image image;
    @Column
    private Integer imageOrder;
}
