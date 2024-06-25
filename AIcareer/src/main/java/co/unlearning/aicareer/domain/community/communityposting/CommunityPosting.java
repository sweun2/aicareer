package co.unlearning.aicareer.domain.community.communityposting;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String uid;
    @CreationTimestamp
    @Column
    private LocalDateTime uploadDate = LocalDateTime.now(); //업로드 날짜
    @Column
    private LocalDateTime lastModified;
    @Column
    private String title; //제목
    @Column(columnDefinition = "TEXT")
    private String content; //내용
    @Column
    private Boolean isView;
    @Column
    private Integer hits; //조회수
    @Column
    private Integer commentCnt;
    @Column
    private Integer reportCnt;
    @Column
    private Integer recommendCnt;
    @OneToMany(mappedBy = "communityPosting",fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<CommunityComment> communityCommentSet;
    @OneToMany(mappedBy = "communityPosting",fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<CommunityPostingUser> communityPostingUserSet;
    @ManyToOne(fetch = FetchType.EAGER) //글쓴이
    private User writer;
    @OneToMany(mappedBy ="communityPosting",fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CommunityPostingImage> subImages;
    @OneToOne(mappedBy = "communityPosting",fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private CommunityVote communityVote;
    @Column
    private Boolean isAnonymous;
}
