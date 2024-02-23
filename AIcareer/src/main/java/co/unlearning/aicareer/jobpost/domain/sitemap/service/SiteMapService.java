package co.unlearning.aicareer.jobpost.domain.sitemap.service;

import co.unlearning.aicareer.jobpost.domain.board.Board;
import co.unlearning.aicareer.jobpost.domain.recruitment.Recruitment;
import co.unlearning.aicareer.jobpost.domain.sitemap.SiteMap;
import co.unlearning.aicareer.jobpost.domain.sitemap.repository.SiteMapRepository;
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

    public void registerBoardSiteMap(Board board) {
        String uid = board.getUid();
        LocalDateTime lastModified = board.getLastModified();
        String urlPrefix = "/board/";

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
        if (!(param instanceof Recruitment || param instanceof Board)) {
            throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
        }



        if (param instanceof Recruitment recruitment) {
            SiteMap siteMap = siteMapRepository.findByUid(recruitment.getUid()).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
            );
            siteMapRepository.delete(siteMap);
        } else {
            Board board = (Board) param;
            SiteMap siteMap = siteMapRepository.findByUid(board.getUid()).orElseThrow(
                    ()-> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
            );
            siteMapRepository.delete(siteMap);
        }
    }
}
