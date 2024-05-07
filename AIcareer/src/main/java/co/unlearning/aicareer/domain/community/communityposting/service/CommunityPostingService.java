package co.unlearning.aicareer.domain.community.communityposting.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingRequirementDto;
import co.unlearning.aicareer.domain.community.communityposting.repository.CommunityPostingRepository;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.repository.CommunityPostingUserRepository;
import co.unlearning.aicareer.domain.community.communitypostinguser.service.CommunityPostingUserService;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityPostingService {
    private final UserService userService;
    private final CommunityPostingRepository communityPostingRepository;
    private final CommunityPostingUserService communityPostingUserService;
    private final CommunityPostingUserRepository communityPostingUserRepository;
    public CommunityPosting addCommunityPost(CommunityPostingRequirementDto.CommunityPostingPost communityPostingPost) {
        User user = userService.getLoginUser();
        isNonBlockedCommunityUser(user);

        CommunityPosting communityPosting = CommunityPosting.builder()
                .uid(UUID.randomUUID().toString())
                .uploadDate(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .title(communityPostingPost.getTitle())
                .content(communityPostingPost.getContent())
                .isView(true)
                .commentCnt(0)
                .recommendCnt(0)
                .reportCnt(0)
                .hits(0)
                .communityCommentSet(new HashSet<>())
                .communityPostingUserSet(new HashSet<>())
                .writer(user)
                .build();

        CommunityPostingUser communityPostingUser = CommunityPostingUser.builder()
                .communityPosting(communityPosting)
                .user(user)
                .isReport(false)
                .isRecommend(false)
                .build();
        communityPosting.getCommunityPostingUserSet().add(communityPostingUser);

        return communityPostingRepository.save(communityPosting);
    }
    public CommunityPosting updateCommunityPost(String uid, CommunityPostingRequirementDto.CommunityPostingPost communityPostingPost) {
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid);
        //수정 가능한지 체크
        isNonBlockedCommunityUser(user);
        if(user != communityPostingUserService.getCommunityPostingUserByCommentUserAndPosting(communityPosting,user).getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }

        communityPosting.setLastModified(LocalDateTime.now());
        communityPosting.setTitle(communityPostingPost.getTitle());
        communityPosting.setContent(communityPostingPost.getContent());

        if(communityPostingPost.getIsView()!= null) {
            userService.checkAdmin();
            communityPosting.setIsView(communityPosting.getIsView());
        }

        return communityPostingRepository.save(communityPosting);
    }
    public void deleteCommunityPostByUid(String uid) {
        User user  = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid);

        //수정 가능한지 체크
        isNonBlockedCommunityUser(user);
        if(user != communityPostingUserService.getCommunityPostingUserByCommentUserAndPosting(communityPosting,user).getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }

        communityPostingRepository.delete(communityPosting);
    }
    public Boolean isNonBlockedCommunityUser(User user) {
        if (user.getUserRole() == UserRole.PERMANENT_BLOCK)
            throw new BusinessException(ResponseErrorCode.USER_BLOCKED);
        else
            return true;
    }
    public CommunityPosting getCommunityPostingByUid(String uid) {
        return communityPostingRepository.findByUid(uid).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
    public List<CommunityPosting> getAllCommunityPostingIsViewTrue(Pageable pageable) {
        return communityPostingRepository.findAllByIsViewTrueOrderByUploadDateDesc(pageable).stream().toList();
    }
    public List<CommunityPosting> getAllCommunityPostingSearchByKeyword(String keyword, Pageable pageable) {
        return communityPostingRepository.findAllByContentContainsOrTitleContains(keyword,keyword,pageable).stream().toList();
    }
    public CommunityPostingUser recommendCommunityPosting(String uid,Boolean status) {
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid);
        Optional<CommunityPostingUser> communityPostingUserOptional = communityPostingUserRepository.findCommunityPostingUserByCommunityPostingAndUser(communityPosting,user);
        CommunityPostingUser communityPostingUser;

        if(communityPostingUserOptional.isEmpty()) {
            communityPostingUser = CommunityPostingUser.builder()
                    .communityPosting(communityPosting)
                    .user(user)
                    .isRecommend(false)
                    .isReport(false)
                    .build();
            communityPosting.getCommunityPostingUserSet().add(communityPostingUser);
        } else communityPostingUser = communityPostingUserOptional.get();

        if(communityPostingUser.getIsRecommend() && status) {
            communityPostingUser.setIsRecommend(true);
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_RECOMMEND);
        } else if(communityPostingUser.getIsRecommend() && !status){
            communityPostingUser.setIsRecommend(false);
            communityPosting.setRecommendCnt(communityPosting.getRecommendCnt()-1);
        } else if(!communityPostingUser.getIsRecommend() && status) {
            communityPostingUser.setIsRecommend(true);
            communityPosting.setRecommendCnt(communityPosting.getRecommendCnt()+1);
        }

        communityPostingRepository.save(communityPosting);
        return communityPostingUser;
    }
    public CommunityPostingUser reportCommunityPosting(String uid,Boolean status) {
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid);
        Optional<CommunityPostingUser> communityPostingUserOptional = communityPostingUserRepository.findCommunityPostingUserByCommunityPostingAndUser(communityPosting,user);
        CommunityPostingUser communityPostingUser;

        if(communityPostingUserOptional.isEmpty()) {
            communityPostingUser = CommunityPostingUser.builder()
                    .communityPosting(communityPosting)
                    .user(user)
                    .isRecommend(false)
                    .isReport(false)
                    .build();
            communityPosting.getCommunityPostingUserSet().add(communityPostingUser);
        } else communityPostingUser = communityPostingUserOptional.get();

        if(communityPostingUser.getIsReport() && status) {
            communityPostingUser.setIsReport(true);
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_REPORT);
        } else if(communityPostingUser.getIsReport() && !status){
            communityPostingUser.setIsReport(false);
            communityPosting.setRecommendCnt(communityPosting.getReportCnt()-1);
        } else if(!communityPostingUser.getIsReport() && status) {
            communityPostingUser.setIsReport(true);
            communityPosting.setReportCnt(communityPosting.getReportCnt()+1);
            if(communityPosting.getReportCnt()>5) {
                hideCommunityPosting(communityPosting);
            }
        }
        communityPostingRepository.save(communityPosting);
        return communityPostingUser;
    }
    public void hideCommunityPosting(CommunityPosting communityPosting) {
        communityPosting.setIsView(false);
        communityPostingRepository.save(communityPosting);
    }
    public List<CommunityPosting> getTopPostsForToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        Pageable topThree = PageRequest.of(0, 3);
        return communityPostingRepository.findTopPosts(startOfDay, endOfDay, topThree);
    }
    public void updatePostingHits (String uid) {
        CommunityPosting communityPosting = getCommunityPostingByUid(uid);
        communityPosting.setHits(communityPosting.getHits()+1);

        communityPostingRepository.save(communityPosting);
    }
}
