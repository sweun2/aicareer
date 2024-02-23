package co.unlearning.aicareer.domain.common.sitemap.service;

import co.unlearning.aicareer.domain.blog.board.BlogBoard;
import co.unlearning.aicareer.domain.common.sitemap.SiteMap;
import co.unlearning.aicareer.domain.common.sitemap.repository.SiteMapRepository;
import co.unlearning.aicareer.domain.job.jobboard.JobBoard;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SiteMapService {
    private final SiteMapRepository siteMapRepository;
    @Value("${front-url}")
    public String siteUrl;

    public List<SiteMap> findSiteMapsLastModifiedWithinOneYear() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        return siteMapRepository.findSiteMapsLastModifiedWithinOneYear(oneYearAgo);
    }
    public List<SiteMap> findAllSiteMap() {
        return siteMapRepository.findAll();
    }

    public void registerRecruitmentSiteMap(Recruitment recruitment) {
        String uid = recruitment.getUid();
        LocalDateTime lastModified = recruitment.getLastModified();
        String urlPrefix = "/recruitment/";

        processSiteMap(uid, lastModified, urlPrefix);
    }

    public void registerJobBoardSiteMap(JobBoard jobBoard) {
        String uid = jobBoard.getUid();
        LocalDateTime lastModified = jobBoard.getLastModified();
        String urlPrefix = "/job/board/";

        processSiteMap(uid, lastModified, urlPrefix);
    }
    public void registerBlogBoardSiteMap(BlogBoard blogBoard) {
        String uid = blogBoard.getUid();
        LocalDateTime lastModified = blogBoard.getLastModified();
        String urlPrefix = "/blog/board/";

        processSiteMap(uid, lastModified, urlPrefix);
    }

    private void processSiteMap(String uid, LocalDateTime lastModified, String urlPrefix) {
        Optional<SiteMap> siteMapOptional = siteMapRepository.findByUid(uid);
        SiteMap siteMap;
        if(siteMapOptional.isEmpty()) {
            siteMap = new SiteMap();
            siteMap.setUid(uid);
            siteMap.setLastModified(lastModified);
            siteMap.setUrl(siteUrl + urlPrefix + uid);
        }else {
            siteMap = siteMapOptional.get();
            siteMap.setLastModified(lastModified);
        }
        siteMapRepository.save(siteMap);
    }

    public void deleteSiteMap(Object param) {
        if (!(param instanceof Recruitment || param instanceof JobBoard)) {
            throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
        }



        if (param instanceof Recruitment recruitment) {
            SiteMap siteMap = siteMapRepository.findByUid(recruitment.getUid()).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
            );
            siteMapRepository.delete(siteMap);
        } else {
            JobBoard jobBoard = (JobBoard) param;
            SiteMap siteMap = siteMapRepository.findByUid(jobBoard.getUid()).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
            );
            siteMapRepository.delete(siteMap);
        }
    }
}
