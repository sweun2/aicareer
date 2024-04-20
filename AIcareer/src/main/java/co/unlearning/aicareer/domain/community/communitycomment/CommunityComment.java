package co.unlearning.aicareer.domain.community.communitycomment;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    @Column
    private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
    @Column(unique = true)
    private String uid;
    @Column
    private LocalDateTime lastModified;
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @Column
    private Boolean isView;
    @ManyToOne
    private CommunityPosting communityPosting;
    @Column
    private Integer reportCnt;
    @Column
    private Integer recommendCnt;
    @OneToMany(mappedBy = "communityComment",fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<CommunityCommentUser> communityCommentUserSet;
    @ManyToOne
    private User writer;
}

