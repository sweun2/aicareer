package co.unlearning.aicareer.domain.community.communityvote;

import co.unlearning.aicareer.domain.common.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private User user;
    @ManyToOne
    private VoteOption voteOption;
    @ManyToOne
    private CommunityVote communityVote;
}
