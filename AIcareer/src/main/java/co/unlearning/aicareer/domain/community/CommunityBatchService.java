package co.unlearning.aicareer.domain.community;

import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitycommentuser.service.CommunityCommentUserService;
import co.unlearning.aicareer.domain.community.communitypostinguser.service.CommunityPostingUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityBatchService {
    private final CommunityPostingUserService communityPostingUserService;
    private final CommunityCommentUserService communityCommentUserService;
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteAllOptionFalsePostingUserAndCommentUserNotWriter() {
        communityPostingUserService.deleteCommunityPostingUserOptionFalse();
        communityCommentUserService.deleteCommunityCommentUserOptionFalse();
    }
}
