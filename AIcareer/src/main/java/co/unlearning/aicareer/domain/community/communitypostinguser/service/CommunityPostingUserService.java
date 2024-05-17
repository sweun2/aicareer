package co.unlearning.aicareer.domain.community.communitypostinguser.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.repository.CommunityPostingUserRepository;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityPostingUserService {
    private final CommunityPostingUserRepository communityPostingUserRepository;
    private final UserService userService;

    public CommunityPostingUser getCommunityPostingUserByCommentUserAndPosting (CommunityPosting communityPosting, User user) {
        return communityPostingUserRepository.findCommunityPostingUserByCommunityPostingAndUser(communityPosting,user).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public CommunityPostingUser getMockCommunityPostingUserFromLoginUser(CommunityPosting communityPosting) {
        CommunityPostingUser communityPostingUser;
        if(userService.isLogin()) {
            Optional<CommunityPostingUser> communityPostingUserOptional = communityPostingUserRepository.findCommunityPostingUserByCommunityPostingAndUser(communityPosting,userService.getLoginUser());
            communityPostingUser = communityPostingUserOptional.orElseGet(() -> CommunityPostingUser.builder()
                    .user(userService.getLoginUser())
                    .isReport(false)
                    .isRecommend(false)
                    .communityPosting(communityPosting)
                    .build());
            communityPostingUserRepository.save(communityPostingUser);
        } else {
            communityPostingUser = CommunityPostingUser.builder()
                    .user(null)
                    .isReport(false)
                    .isRecommend(false)
                    .communityPosting(communityPosting)
                    .build();
        }
        return communityPostingUser;
    }
}
