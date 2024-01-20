package co.unlearning.aicareer.domain.bookmark;

import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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
