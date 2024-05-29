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
import co.unlearning.aicareer.global.security.jwt.TokenService;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.*;
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
    private final TokenService tokenService;
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

    private void updateSubImages(CommunityPosting communityPosting, List<String> newSubImageUrls) {
        List<CommunityPostingImage> currentCommunityPostingImages = communityPosting.getSubImages().stream()
                .filter(communityPostingImage -> communityPostingImage.getImageOrder() != null && communityPostingImage.getImageOrder() != 0)
                .collect(Collectors.toList());

        if (!currentCommunityPostingImages.isEmpty()) {
            currentCommunityPostingImages.forEach(bi -> {
                bi.getImage().setIsRelated(false);
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
    public Map.Entry<CommunityPosting,CommunityPostingUser> updateIsView(CommunityPostingRequirementDto.CommunityPostingIsView communityPostingIsView) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(communityPostingIsView.getUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );

        if(communityPostingIsView.getIsView()!= null) {
            userService.checkAdmin();
            communityPosting.setIsView(communityPostingIsView.getIsView());
        }
        return Map.entry(communityPostingRepository.save(communityPosting),communityPostingUserService.getMockCommunityPostingUserFromLoginUser(communityPosting));
    }
    public void deleteCommunityPostByUid(String uid) {
        User user  = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();

        //수정 가능한지 체크
        isNonBlockedCommunityUser(user);
        if(user == communityPosting.getWriter() || user.getUserRole() == UserRole.ADMIN) {
            communityPosting.getSubImages().forEach(
                    communityPostingImageService::removeCommunityPostingImage
            );

            siteMapService.deleteSiteMap(communityPosting);
            communityPostingRepository.delete(communityPosting);
        } else throw new BusinessException(ResponseErrorCode.USER_NOT_ALLOWED);
    }

    public void isNonBlockedCommunityUser(User user) {
        if (user.getUserRole() == UserRole.PERMANENT_BLOCK)
            throw new BusinessException(ResponseErrorCode.USER_BLOCKED);
    }
    public Map.Entry<CommunityPosting,CommunityPostingUser> getCommunityPostingByUid(String uid) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(uid).orElseThrow(
                ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(!communityPosting.getIsView()) {
            userService.checkAdmin();
        }

        CommunityPostingUser communityPostingUser;
        communityPostingUser = communityPostingUserService.getMockCommunityPostingUserFromLoginUser(communityPosting);
        return Map.entry(communityPosting,communityPostingUser);
    }


    public List<CommunityPosting> getAllCommunityPosting(Pageable pageable) {
        if(userService.isLogin()) {
            User user = userService.getLoginUser();
            if(user.getUserRole() == UserRole.ADMIN) {
                return communityPostingRepository.findAllByOrderByUploadDateDesc(pageable).stream().toList();
            }
        }
        return communityPostingRepository.findAllByIsViewTrueOrderByUploadDateDesc(pageable).stream().toList();
    }
    public List<CommunityPosting> getAllCommunityPostingSearchByKeyword(String keyword, Pageable pageable) {
        if(userService.isLogin()) {
            User user = userService.getLoginUser();
            if(user.getUserRole() == UserRole.ADMIN) {
                return communityPostingRepository.findAllByContentContainsOrTitleContains(keyword, keyword,pageable).stream().toList();
            }
        }
        return communityPostingRepository.findAllByContentContainsOrTitleContainsAndIsViewTrue(keyword, keyword,pageable).stream().toList();
    }
    public CommunityPostingUser recommendCommunityPosting(String uid,Boolean status) {
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

        if(status) {
            if (!communityPostingUser.getIsRecommend()) {
                communityPosting.setRecommendCnt(communityPosting.getRecommendCnt()+1);
                communityPostingUser.setIsRecommend(true);
            }
        } else {
            if (communityPostingUser.getIsRecommend()) {
                communityPosting.setRecommendCnt(communityPosting.getRecommendCnt()-1);
                communityPostingUser.setIsRecommend(false);
            }
        }

        communityPostingRepository.save(communityPosting);
        return communityPostingUser;
    }
    public CommunityPostingUser reportCommunityPosting(String uid) {
        log.info("repost");
        User user = userService.getLoginUser();
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();
        log.info(communityPosting.getUid());

        Optional<CommunityPostingUser> communityPostingUserOptional = communityPostingUserRepository.findCommunityPostingUserByCommunityPostingAndUser(communityPosting,user);
        CommunityPostingUser communityPostingUser;

        if(communityPostingUserOptional.isEmpty()) {
            communityPostingUser = CommunityPostingUser.builder()
                    .communityPosting(communityPosting)
                    .user(user)
                    .isRecommend(false)
                    .isReport(false)
                    .build();

            communityPostingUserRepository.save(communityPostingUser);
            communityPosting.getCommunityPostingUserSet().add(communityPostingUser);
        } else communityPostingUser = communityPostingUserOptional.get();

        if (!communityPostingUser.getIsReport()) {
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
        return communityPostingRepository.findTopPostsWithIsViewTrue(startOfDay, endOfDay, topThree);
    }
    public void updatePostingHits(HttpServletRequest request, HttpServletResponse response, String uid) {
        String cookieValue = null;

        // 쿠키에서 토큰 가져오기
        HttpServletRequest request2 = (HttpServletRequest) request;
        if (request2 != null) {
            Cookie[] cookies = request2.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if(Objects.equals(cookie.getName(), "_cP")) {
                        cookieValue = cookie.getValue();
                    }
                }
            }
        }

        // 현재 날짜와 자정 시간을 합쳐서 당일 자정의 LocalDateTime을 생성
        LocalDateTime todayMidnight = LocalDate.now().atTime(LocalTime.MIDNIGHT);

        // 토큰이 없으면 새로 생성하여 쿠키에 저장
        if (cookieValue == null || Objects.equals(cookieValue, StringUtils.EMPTY)) {
            ResponseCookie cpCookie = ResponseCookie.from("_cP", LocalDateTime.now().toString())
                    .path("/")
                    .sameSite("None")
                    .domain(".aicareer.co.kr")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(Duration.between(ZonedDateTime.now(), todayMidnight.atZone(ZoneId.systemDefault())).getSeconds())
                    .build();

            // 쿠키를 응답 헤더와 HttpHeaders에 추가
            response.addHeader("Set-Cookie", cpCookie.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Set-Cookie", cpCookie.toString());
        } else {
            // 이미 조회수를 증가시킨 경우에는 추가적인 작업을 수행하지 않음
            if (isHitIncreasedToday(cookieValue)) {
                return;
            }
        }

        // 게시글 조회 및 hits 증가
        CommunityPosting communityPosting = getCommunityPostingByUid(uid).getKey();
        communityPosting.setHits(communityPosting.getHits() + 1);

        // 저장
        communityPostingRepository.save(communityPosting);
    }

    private boolean isHitIncreasedToday(String cookieValue) {
        // 쿠키가 존재하지 않는 경우에는 조회수가 증가되지 않은 것으로 판단합니다.
        if (StringUtils.isEmpty(cookieValue)) {
            return false;
        }

        // 쿠키의 값에서 저장된 날짜 정보를 추출합니다.
        LocalDateTime cookieDateTime = LocalDateTime.parse(cookieValue);

        // 쿠키에 저장된 날짜 정보와 오늘 자정을 비교하여 조회수가 이미 증가되었는지 여부를 판단합니다.
        LocalDateTime todayMidnight = LocalDate.now().atTime(LocalTime.MIDNIGHT);
        return cookieDateTime.isEqual(todayMidnight) || cookieDateTime.isAfter(todayMidnight);
    }


}
