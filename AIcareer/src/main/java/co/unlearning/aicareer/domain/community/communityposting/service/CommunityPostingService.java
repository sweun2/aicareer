package co.unlearning.aicareer.domain.community.communityposting.service;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.common.sitemap.service.SiteMapService;
import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserRole;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communityposting.dto.CommunityPostingRequirementDto;
import co.unlearning.aicareer.domain.community.communityposting.repository.CommunityPostingRepository;
import co.unlearning.aicareer.domain.community.communitypostingimage.CommunityPostingImage;
import co.unlearning.aicareer.domain.community.communitypostingimage.repository.CommunityPostingImageRepository;
import co.unlearning.aicareer.domain.community.communitypostingimage.service.CommunityPostingImageService;
import co.unlearning.aicareer.domain.community.communitypostinguser.CommunityPostingUser;
import co.unlearning.aicareer.domain.community.communitypostinguser.repository.CommunityPostingUserRepository;
import co.unlearning.aicareer.domain.community.communitypostinguser.service.CommunityPostingUserService;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.persistence.EntityManager;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityPostingService {
    private final UserService userService;
    private final SiteMapService siteMapService;
    private final CommunityPostingRepository communityPostingRepository;
    private final CommunityPostingUserService communityPostingUserService;
    private final CommunityPostingUserRepository communityPostingUserRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final CommunityPostingImageService communityPostingImageService;
    private final CommunityPostingImageRepository communityPostingImageRepository;
    private final EntityManager entityManager;
    public Map.Entry<CommunityPosting,CommunityPostingUser> addCommunityPost(CommunityPostingRequirementDto.CommunityPostingPost communityPostingPost) {
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


        List<CommunityPostingImage> subImages = new ArrayList<>();
        int order = 0;
        for (String subImageUrl : communityPostingPost.getImageUrls()) {
            Image subImage = imageRepository.findByImageUrl(
                            ImagePathLengthConverter.slicingImagePathLength(subImageUrl))
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            subImage.setIsRelated(true);
            subImages.add(CommunityPostingImage.builder()
                    .image(subImage)
                    .communityPosting(communityPosting)
                    .imageOrder(++order)
                    .build());
        }
        communityPosting.setSubImages(subImages);


        CommunityPostingUser communityPostingUser = CommunityPostingUser.builder()
                .communityPosting(communityPosting)
                .user(user)
                .isReport(false)
                .isRecommend(false)
                .build();
        communityPosting.getCommunityPostingUserSet().add(communityPostingUser);

        communityPostingRepository.save(communityPosting);
        siteMapService.registerCommunityPostingSiteMap(communityPosting);

        return Map.entry(communityPosting,communityPostingUser);
    }
    public Map.Entry<CommunityPosting,CommunityPostingUser> updateCommunityPost(String uid, CommunityPostingRequirementDto.CommunityPostingPost communityPostingPost) {
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();
        CommunityPostingUser communityPostingUser = communityPostingUserRepository.findCommunityPostingUserByCommunityPostingAndUser(communityPosting,user).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        //수정 가능한지 체크
        isNonBlockedCommunityUser(user);
        if(user != communityPostingUser.getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }

        // Update sub images
        updateSubImages(communityPosting, communityPostingPost.getImageUrls());

        communityPosting.setLastModified(LocalDateTime.now());
        communityPosting.setTitle(communityPostingPost.getTitle());
        communityPosting.setContent(communityPostingPost.getContent());


        communityPostingRepository.save(communityPosting);

        return Map.entry(communityPosting,communityPostingUser);
    }
    private void updateMainImage(String newImageUrl, CommunityPostingImage currentCommunityPostingImage, Consumer<CommunityPostingImage> setCommunityPostingImage) {
        if (newImageUrl != null && !newImageUrl.isEmpty()) {
            Image newImage = imageRepository.findByImageUrl(ImagePathLengthConverter.slicingImagePathLength(newImageUrl))
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            if (currentCommunityPostingImage != null && !newImage.getImageUrl().equals(currentCommunityPostingImage.getImage().getImageUrl())) {
                currentCommunityPostingImage.getImage().setIsRelated(false);
                communityPostingImageService.removeCommunityPostingImage(currentCommunityPostingImage);

                CommunityPostingImage communityPostingImage = CommunityPostingImage.builder()
                        .imageOrder(0)
                        .image(newImage)
                        .communityPosting(currentCommunityPostingImage.getCommunityPosting())
                        .build();
                newImage.setIsRelated(true);
                setCommunityPostingImage.accept(communityPostingImage);
                communityPostingImageRepository.save(communityPostingImage);
            }
        } else if (currentCommunityPostingImage != null) {
            currentCommunityPostingImage.getImage().setIsRelated(false);
            communityPostingImageService.removeCommunityPostingImage(currentCommunityPostingImage);
            setCommunityPostingImage.accept(null);
            entityManager.flush();
        }
    }

    private void updateSubImages(CommunityPosting communityPosting, List<String> newSubImageUrls) {
        List<CommunityPostingImage> currentCommunityPostingImages = communityPosting.getSubImages().stream()
                .filter(communityPostingImage -> communityPostingImage.getImageOrder() != null && communityPostingImage.getImageOrder() != 0)
                .collect(Collectors.toList());

        if (!currentCommunityPostingImages.isEmpty()) {
            currentCommunityPostingImages.forEach(bi -> {
                bi.getImage().setIsRelated(false);
                imageService.deleteImageByUrl(bi.getImage().getImageUrl());
            });
            communityPostingImageRepository.deleteAll(currentCommunityPostingImages);
            entityManager.flush();
        }

        communityPosting.getSubImages().removeAll(currentCommunityPostingImages);
        communityPostingRepository.save(communityPosting);

        for (int order = 0; order < newSubImageUrls.size(); order++) {
            String imageUrl = newSubImageUrls.get(order);
            String slicedImageUrl = ImagePathLengthConverter.slicingImagePathLength(imageUrl);
            Image image = imageRepository.findByImageUrl(slicedImageUrl)
                    .orElseThrow(() -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL));
            Optional<CommunityPostingImage> communityPostingImageOptional = communityPostingImageRepository.findByImage(image);
            if (communityPostingImageOptional.isEmpty()) {
                CommunityPostingImage newCommunityPostingImage = CommunityPostingImage.builder()
                        .communityPosting(communityPosting)
                        .image(image)
                        .imageOrder(order + 1)
                        .build();
                image.setIsRelated(true);
                communityPosting.getSubImages().add(newCommunityPostingImage);
                communityPostingImageRepository.save(newCommunityPostingImage);
            } else {
                communityPostingImageOptional.get().setImageOrder(order + 1);
                communityPostingImageRepository.save(communityPostingImageOptional.get());
            }
        }
        communityPostingRepository.save(communityPosting);
    }
    public CommunityPosting updateIsView(CommunityPostingRequirementDto.CommunityPostingIsView communityPostingIsView) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(communityPostingIsView.getUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );

        if(communityPostingIsView.getIsView()!= null) {
            userService.checkAdmin();
            communityPosting.setIsView(communityPostingIsView.getIsView());
        }
        return communityPostingRepository.save(communityPosting);
    }
    public void deleteCommunityPostByUid(String uid) {
        User user  = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();

        //수정 가능한지 체크
        isNonBlockedCommunityUser(user);
        if(user != communityPostingUserService.getCommunityPostingUserByCommentUserAndPosting(communityPosting,user).getUser()) {
            throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
        }
        communityPosting.getSubImages().forEach(
                communityPostingImageService::removeCommunityPostingImage
        );

        siteMapService.deleteSiteMap(communityPosting);
        communityPostingRepository.delete(communityPosting);
    }

    public void isNonBlockedCommunityUser(User user) {
        if (user.getUserRole() == UserRole.PERMANENT_BLOCK)
            throw new BusinessException(ResponseErrorCode.USER_BLOCKED);
    }
    public Map.Entry<CommunityPosting,CommunityPostingUser> getCommunityPostingByUid(String uid) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(uid).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        CommunityPostingUser communityPostingUser;
        communityPostingUser = communityPostingUserService.getMockCommunityPostingUserFromLoginUser(communityPosting);
        return Map.entry(communityPosting,communityPostingUser);
    }


    public List<CommunityPosting> getAllCommunityPostingIsViewTrue(Pageable pageable) {
        return communityPostingRepository.findAllByIsViewTrueOrderByUploadDateDesc(pageable).stream().toList();
    }
    public List<CommunityPosting> getAllCommunityPostingSearchByKeyword(String keyword, Pageable pageable) {
        return communityPostingRepository.findAllByContentContainsOrTitleContains(keyword,keyword,pageable).stream().toList();
    }
    public CommunityPostingUser recommendCommunityPosting(String uid) {
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();
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

        if(communityPostingUser.getIsRecommend()) {
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_RECOMMEND);
        } else {
            communityPosting.setRecommendCnt(communityPosting.getRecommendCnt()+1);
            communityPostingUser.setIsRecommend(true);
        }

        communityPostingRepository.save(communityPosting);
        return communityPostingUser;
    }
    public CommunityPostingUser reportCommunityPosting(String uid) {
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();
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

        if(communityPostingUser.getIsReport()) {
            throw new BusinessException(ResponseErrorCode.USER_ALREADY_REPORT);
        } else {
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
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();
        communityPosting.setHits(communityPosting.getHits()+1);

        communityPostingRepository.save(communityPosting);
    }
}
