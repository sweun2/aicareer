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
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<CommunityComment> getCommunityCommentsByCommunityPosting (String uid, Pageable pageable) {
        CommunityPosting communityPosting = communityPostingService.getCommunityPostingByUid(uid);
        return communityCommentRepository.findAllByCommunityPostingAndIsViewTrueOrderByUploadDateDesc(communityPosting,pageable).stream().toList();
    }
    public CommunityComment addCommunityComment(CommunityCommentRequirementDto.CommunityCommentPost communityCommentPost){
        User user = userService.getLoginUser();
        communityPostingService.isNonBlockedCommunityUser(user);
        CommunityPosting communityPosting = communityPostingService.getCommunityPostingByUid(communityCommentPost.getPostingUid());

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
        return communityCommentRepository.save(communityComment);
    }
    public CommunityComment updateCommunityComment(String commentUid, CommunityCommentRequirementDto.CommunityCommentPost communityCommentPost) {
        User user = userService.getLoginUser();
        communityPostingService.isNonBlockedCommunityUser(user);
        CommunityComment communityComment = communityCommentRepository.findByUid(commentUid).orElseThrow(
                ()->new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(user != communityCommentUserService.getCommunityCommentUserByCommunityComment(communityComment).getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }

        communityComment.setContent(communityCommentPost.getContent());

        if(communityCommentPost.getIsView()!= null) {
            userService.checkAdmin();
            communityComment.setIsView(communityComment.getIsView());
        }
        return communityCommentRepository.save(communityComment);
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
    public CommunityCommentUser recommendCommunityComment(String uid,Boolean status) {
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

        if(communityCommentUser.getIsRecommend() && status) {
            communityCommentUser.setIsRecommend(true);
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_RECOMMEND);
        } else if(communityCommentUser.getIsRecommend() && !status){
            communityCommentUser.setIsRecommend(false);
            communityComment.setRecommendCnt(communityComment.getRecommendCnt()-1);
        } else if(!communityCommentUser.getIsRecommend() && status) {
            communityCommentUser.setIsRecommend(true);
            communityComment.setRecommendCnt(communityComment.getRecommendCnt()+1);
        }
        communityCommentRepository.save(communityComment);
        return communityCommentUser;
    }
    public CommunityCommentUser reportCommunityComment(String uid,Boolean status) {
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

        if(communityCommentUser.getIsReport() && status) {
            communityCommentUser.setIsReport(true);
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_REPORT);
        } else if(communityCommentUser.getIsReport() && !status){
            communityCommentUser.setIsReport(false);
            communityComment.setRecommendCnt(communityComment.getReportCnt()-1);
        } else if(!communityCommentUser.getIsReport() && status) {
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
