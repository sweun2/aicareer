package co.unlearning.aicareer.jobpost.domain.user;

import co.unlearning.aicareer.jobpost.domain.bookmark.Bookmark;
import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Column
    private String nickname;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String recommender;
    @CreationTimestamp
    @Column
    private LocalDateTime joinDate; //joinDate
    @Column
    private UserRole userRole;
    @OneToMany(mappedBy = "user")
    private Set<Bookmark> bookmarkSet;
}
