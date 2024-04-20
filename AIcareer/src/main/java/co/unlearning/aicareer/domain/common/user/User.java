package co.unlearning.aicareer.domain.common.user;

import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.job.bookmark.Bookmark;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private UserInterest userInterest;
    @Column
    private Boolean isInterest;
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    private UserTerms isMarketing;
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    private UserTerms isAgreeInformationTerms;
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    private UserTerms isAgreeUseTerms;
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    private UserTerms isAgreePrivacyTerms;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private Set<CommunityPostingUser> communityPostingUser;
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private Set<CommunityCommentUser> communityCommentUserSet;
}
