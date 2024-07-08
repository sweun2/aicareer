package co.unlearning.aicareer.domain.community.communitypostinguser;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"communityPosting_id", "user_id"}))
public class CommunityPostingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private CommunityPosting communityPosting;
    @ManyToOne
    private User user;
    @Column
    private Boolean isReport;
    @Column
    private Boolean isRecommend;
}