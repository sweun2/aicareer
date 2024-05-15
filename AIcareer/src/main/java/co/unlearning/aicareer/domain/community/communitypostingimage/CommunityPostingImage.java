package co.unlearning.aicareer.domain.community.communitypostingimage;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostingImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private CommunityPosting communityPosting;
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Image image;
    @Column
    private Integer imageOrder;
}
