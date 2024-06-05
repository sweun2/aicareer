package co.unlearning.aicareer.domain.community.communityvote.service;

import co.unlearning.aicareer.domain.community.communityvote.dto.CommunityVoteRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityVoteService {
    public void createVote(CommunityVoteRequestDto.VotePost votePost) {
        log.info("createVote");

    }
}
