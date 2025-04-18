package co.unlearning.aicareer.domain.common.seo.sitemap.dto;

import co.unlearning.aicareer.domain.common.seo.sitemap.SiteMap;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SiteMapResponseDto {
    @Getter
    @Setter
    @Builder
    public static class SiteMapInfo {
        private String url;
        private String lastModified;
        public static SiteMapInfo of(SiteMap siteMap) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedLastModified = siteMap.getLastModified().format(formatter);

            return SiteMapInfo.builder()
                    .url(siteMap.getUrl())
                    .lastModified(formattedLastModified)
                    .build();
        }
        public static List<SiteMapInfo> of(List<SiteMap> siteMaps) {
            return siteMaps.stream().map(SiteMapInfo::of).collect(Collectors.toList());
        }
    }
}
