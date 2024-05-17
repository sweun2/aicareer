package co.unlearning.aicareer.domain.community.communitycommentuser.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycomment.repository.CommunityCommentRepository;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitycommentuser.repository.CommunityCommentUserRepository;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.repository.CommunityPostingUserRepository;
import co.unlearning.aicareer.domain.community.communitypostinguser.service.CommunityPostingUserService;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityCommentUserService {
    private final CommunityCommentUserRepository communityCommentUserRepository;
    private final UserService userService;
    public CommunityCommentUser getCommunityCommentUserByCommunityComment (CommunityComment communityComment) {
        return communityCommentUserRepository.findByCommunityComment(communityComment).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public CommunityCommentUser getMockCommunityCommentUserFromLoginUser(CommunityComment communityComment) {
        CommunityCommentUser communityCommentUser;
        if(userService.isLogin()) {
            Optional<CommunityCommentUser> communityCommentUserOptional = communityCommentUserRepository.findByUserAndCommunityComment(userService.getLoginUser(),communityComment);
            communityCommentUser = communityCommentUserOptional.orElseGet(() -> CommunityCommentUser.builder()
                    .user(userService.getLoginUser())
                    .isReport(false)
                    .isRecommend(false)
                    .communityComment(communityComment)
                    .build());
            communityCommentUserRepository.save(communityCommentUser);
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
}
