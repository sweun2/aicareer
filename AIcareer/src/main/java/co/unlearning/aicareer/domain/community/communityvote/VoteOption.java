package co.unlearning.aicareer.domain.community.communityvote;

import co.unlearning.aicareer.domain.common.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String option;
    @Column
    private Integer voteCnt;
    @ManyToOne
    private CommunityVote communityVote;
    @OneToMany(mappedBy = "voteOption")
    private Set<VoteUser> voteUserSet;
}
