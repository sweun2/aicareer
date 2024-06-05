package co.unlearning.aicareer.domain.community.communityvote;

import jakarta.persistence.*;
import lombok.*;

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
}
