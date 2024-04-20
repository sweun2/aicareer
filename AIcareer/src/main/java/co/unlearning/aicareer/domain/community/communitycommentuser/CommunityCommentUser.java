package co.unlearning.aicareer.domain.community.communitycommentuser;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private CommunityComment communityComment;
    @ManyToOne
    private User user;
    @Column
    private Boolean isReport;
    @Column
    private Boolean isRecommend;
}