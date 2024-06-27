package co.unlearning.aicareer.domain.community.communitycommentuser.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitycommentuser.repository.CommunityCommentUserRepository;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityCommentUserService {
    private final CommunityCommentUserRepository communityCommentUserRepository;
    private final UserService userService;
    public List<CommunityCommentUser> getCommunityCommentUserByCommunityComment (CommunityComment communityComment) {
        return communityCommentUserRepository.findByCommunityComment(communityComment);
    }
    public CommunityCommentUser getCommunityCommentUserByUserAndCommunityComment(User user, CommunityComment communityComment) {
        Optional<CommunityCommentUser> communityCommentUserOptional = communityCommentUserRepository.findByUserAndCommunityComment(user,communityComment);
        return communityCommentUserOptional.orElseThrow(() -> new BusinessException(ResponseErrorCode.COMMENT_USER_NOT_FOUND));
    }
    public CommunityCommentUser getMockCommunityCommentUserIfNotLogin(CommunityComment communityComment) {
        CommunityCommentUser communityCommentUser;
        if(userService.isLogin()) {
            Optional<CommunityCommentUser> communityCommentUserOptional = communityCommentUserRepository.findByUserAndCommunityComment(userService.getLoginUser(),communityComment);
            communityCommentUser = communityCommentUserOptional.orElseGet(() -> CommunityCommentUser.builder()
                    .user(userService.getLoginUser())
                    .isReport(false)
                    .isRecommend(false)
                    .communityComment(communityComment)
                    .build());
        } else {
            communityCommentUser = CommunityCommentUser.builder()
                    .user(null)
                    .isReport(false)
                    .isRecommend(false)
                    .communityComment(communityComment)
                    .build();
        }
        return communityCommentUser;
    }
    public void deleteCommunityCommentUserOptionFalse() {
        List<CommunityCommentUser> communityCommentUsers = communityCommentUserRepository.findCommunityCommentUserAllOptionFalseAndNotWriter();
        communityCommentUserRepository.deleteAll(communityCommentUsers);
    }
}
