package co.unlearning.aicareer.domain.common.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserTerms {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ColumnDefault("0")
    private Boolean isAgree;
    @CreationTimestamp
    @Column
    private LocalDateTime agreeDate; //동의 시점
    @OneToOne
    private User user;
}
