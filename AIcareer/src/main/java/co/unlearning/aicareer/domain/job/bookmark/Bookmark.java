package co.unlearning.aicareer.domain.job.bookmark;

import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.common.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recruitment_id", "user_id"})
})
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private User user;
    @ManyToOne
    @Cascade(CascadeType.PERSIST)
    private Recruitment recruitment;
}
