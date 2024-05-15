package co.unlearning.aicareer.domain.job.recruitmentImage;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.job.board.Board;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Recruitment recruitment;
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Image image;
    @Column
    private Integer imageOrder;
}
