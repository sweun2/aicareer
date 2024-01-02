package co.unlearning.aicareer.domain.user;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ColumnDefault("")
    private String nickname;
    @ColumnDefault("")
    private String name;
    @ColumnDefault("")
    private String email;
    @ColumnDefault("")
    private String password;
    @ColumnDefault("")
    private String recommender;
    @CreationTimestamp
    @Column
    private LocalDateTime joinDate; //joinDate
    @Column
    private UserRole userRole;
}
