package co.unlearning.aicareer.domain.community.communityvote;

import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private Boolean isMultiple;
    @Column
    private Boolean isAnonymous;
    @Column
    private LocalDateTime endDate;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true,mappedBy = "communityVote")
    private List<VoteOption> voteOption;
    @OneToOne
    private CommunityPosting communityPosting;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true,mappedBy = "communityVote")
    private List<VoteUser> voteUser;
}
