package co.unlearning.aicareer.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    public enum Gender {
        Male,Female
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ColumnDefault("0")
    private String name;
    @ColumnDefault("0")
    private String phone;
    @ColumnDefault("0")
    private String password;
    @ColumnDefault("0")
    private Gender gender;
    @ColumnDefault("0")
    private Boolean isSmsAuth;

    @ColumnDefault("0")
    private Boolean isAgreeTerms;
    @ColumnDefault("0")
    private Boolean isMarketing;
    @ColumnDefault("0")
    private String recommender;
    @ColumnDefault("0")
    private String joinDate;
}
