package co.unlearning.aicareer.domain.community.communitycomment.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communitycomment.CommunityComment;
import co.unlearning.aicareer.domain.community.communitycomment.dto.CommunityCommentRequirementDto;
import co.unlearning.aicareer.domain.community.communitycomment.repository.CommunityCommentRepository;
import co.unlearning.aicareer.domain.community.communitycommentuser.CommunityCommentUser;
import co.unlearning.aicareer.domain.community.communitycommentuser.repository.CommunityCommentUserRepository;
import co.unlearning.aicareer.domain.community.communitycommentuser.service.CommunityCommentUserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communityposting.repository.CommunityPostingRepository;
import co.unlearning.aicareer.domain.community.communityposting.service.CommunityPostingService;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import com.amazonaws.transform.MapEntry;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityCommentService {
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityPostingRepository communityPostingRepository;
    private final CommunityPostingService communityPostingService;
    private final CommunityCommentUserService communityCommentUserService;
    private final CommunityCommentUserRepository communityCommentUserRepository;
    private final UserService userService;

    public List<Map.Entry<CommunityComment,CommunityCommentUser>> getCommunityCommentsByCommunityPosting (String uid, Pageable pageable) {
        CommunityPosting communityPosting = communityPostingService.getCommunityPostingByUid(uid).getKey();
        List<Map.Entry<CommunityComment,CommunityCommentUser>> entry = new ArrayList<>();

        communityCommentRepository.findAllByCommunityPostingAndIsViewTrueOrderByUploadDateDesc(communityPosting,pageable).stream().toList()
                .forEach(communityComment -> {
                    CommunityCommentUser communityCommentUser = communityCommentUserService.getMockCommunityCommentUserFromLoginUser(communityComment);
                    entry.add(Map.entry(communityComment,communityCommentUser));
                });

        return entry;
    }
    public Map.Entry<CommunityComment,CommunityCommentUser> addCommunityComment(CommunityCommentRequirementDto.CommunityCommentPost communityCommentPost){
        User user = userService.getLoginUser();
        communityPostingService.isNonBlockedCommunityUser(user);
        CommunityPosting communityPosting = communityPostingService.getCommunityPostingByUid(communityCommentPost.getPostingUid()).getKey();

        CommunityComment communityComment = CommunityComment.builder()
                .uid(UUID.randomUUID().toString())
                .uploadDate(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .content(communityCommentPost.getContent())
                .isView(true)
                .communityPosting(communityPosting)
                .communityCommentUserSet(new HashSet<>())
                .reportCnt(0)
                .recommendCnt(0)
                .writer(user)
                .build();

        CommunityCommentUser communityCommentUser = CommunityCommentUser.builder()
                .communityComment(communityComment)
                .user(user)
                .isReport(false)
                .isRecommend(false)
                .build();
        communityPosting.setCommentCnt(communityPosting.getCommentCnt()+1);
        communityComment.getCommunityCommentUserSet().add(communityCommentUser);

        communityPostingRepository.save(communityPosting);
        communityCommentRepository.save(communityComment);
        return Map.entry(communityComment,communityCommentUser);
    }
    public Map.Entry<CommunityComment,CommunityCommentUser> updateCommunityComment(String commentUid, CommunityCommentRequirementDto.CommunityCommentUpdate communityCommentUpdate) {
        User user = userService.getLoginUser();
        communityPostingService.isNonBlockedCommunityUser(user);
        CommunityComment communityComment = communityCommentRepository.findByUid(commentUid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(user != communityCommentUserService.getCommunityCommentUserByCommunityComment(communityComment).getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }

        communityComment.setContent(communityCommentUpdate.getContent());
        communityCommentRepository.save(communityComment);
        return Map.entry(communityComment, CommunityCommentUser.builder().build());
    }
    public Map.Entry<CommunityComment,CommunityCommentUser> updateIsView(CommunityCommentRequirementDto.CommunityCommentIsView communityCommentIsView) {
        CommunityComment communityComment = communityCommentRepository.findByUid(communityCommentIsView.getUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );

        if(communityCommentIsView.getIsView()!= null) {
            userService.checkAdmin();
            communityComment.setIsView(communityCommentIsView.getIsView());
        }
        communityCommentRepository.save(communityComment);
        return Map.entry(communityComment, CommunityCommentUser.builder().build());
    }


    public void deleteCommunityCommentByUid(String commentUid) {
        User user = userService.getLoginUser();
        communityPostingService.isNonBlockedCommunityUser(user);
        CommunityComment communityComment = communityCommentRepository.findByUid(commentUid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );

        if(user != communityCommentUserService.getCommunityCommentUserByCommunityComment(communityComment).getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }

        communityCommentRepository.delete(communityComment);
    }
    public CommunityCommentUser recommendCommunityComment(String uid) {
        User user = userService.getLoginUser();
        CommunityComment communityComment = getCommunityCommentByUid(uid);
        Optional<CommunityCommentUser> communityCommentUserOptional = communityCommentUserRepository.findByCommunityComment(communityComment);
        CommunityCommentUser communityCommentUser;

        if(communityCommentUserOptional.isEmpty()) {
            communityCommentUser = CommunityCommentUser.builder()
                    .communityComment(communityComment)
                    .user(user)
                    .isRecommend(false)
                    .isReport(false)
                    .build();
            communityComment.getCommunityCommentUserSet().add(communityCommentUser);
        } else communityCommentUser = communityCommentUserOptional.get();

        if(communityCommentUser.getIsRecommend()) {
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_RECOMMEND);
        } else {
            communityCommentUser.setIsRecommend(true);
            communityComment.setRecommendCnt(communityComment.getRecommendCnt()+1);
        }

        communityCommentRepository.save(communityComment);
        return communityCommentUser;
    }
    public CommunityCommentUser reportCommunityComment(String uid) {
        User user = userService.getLoginUser();
        CommunityComment communityComment = getCommunityCommentByUid(uid);
        Optional<CommunityCommentUser> communityCommentUserOptional = communityCommentUserRepository.findByCommunityComment(communityComment);
        CommunityCommentUser communityCommentUser;

        if(communityCommentUserOptional.isEmpty()) {
            communityCommentUser = CommunityCommentUser.builder()
                    .communityComment(communityComment)
                    .user(user)
                    .isRecommend(false)
                    .isReport(false)
                    .build();
            communityComment.getCommunityCommentUserSet().add(communityCommentUser);
        } else communityCommentUser = communityCommentUserOptional.get();

        if(communityCommentUser.getIsReport()) {
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_REPORT);
        } else{
            communityCommentUser.setIsReport(true);
            communityComment.setReportCnt(communityComment.getReportCnt()+1);
            if(communityComment.getReportCnt()>5) {
                hideCommunityComment(communityComment);
            }
        }
        communityCommentRepository.save(communityComment);
        return communityCommentUser;
    }
    public void hideCommunityComment(CommunityComment communityComment) {
        communityComment.setIsView(false);
        communityCommentRepository.save(communityComment);
    }
    public CommunityComment getCommunityCommentByUid(String uid) {
        return communityCommentRepository.findByUid(uid).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
}
