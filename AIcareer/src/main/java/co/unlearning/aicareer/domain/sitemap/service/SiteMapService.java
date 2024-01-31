package co.unlearning.aicareer.domain.sitemap.service;

import co.unlearning.aicareer.domain.board.Board;
import co.unlearning.aicareer.domain.recruitment.Recruitment;
import co.unlearning.aicareer.domain.sitemap.SiteMap;
import co.unlearning.aicareer.domain.sitemap.repository.SiteMapRepository;
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

    public void registerSiteMap(Object param) {
        if (!(param instanceof Recruitment || param instanceof Board)) {
            throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
        }

        String uid;
        LocalDateTime lastModified;
        String urlPrefix;

        if (param instanceof Recruitment recruitment) {
            uid = recruitment.getUid();
            lastModified = recruitment.getLastModified();
            urlPrefix = "/recruitment/";
        } else {
            Board board = (Board) param;
            uid = board.getUid();
            lastModified = board.getLastModified();
            urlPrefix = "/board/";
        }

        Optional<SiteMap> siteMapOptional = siteMapRepository.findByUid(uid);

        SiteMap siteMap = siteMapOptional.orElseGet(SiteMap::new);
        siteMap.setUid(uid);
        siteMap.setLastModified(lastModified);
        siteMap.setUrl(siteUrl + urlPrefix + uid);
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
