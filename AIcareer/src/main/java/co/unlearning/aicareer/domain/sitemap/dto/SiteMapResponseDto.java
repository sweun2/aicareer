package co.unlearning.aicareer.domain.sitemap.dto;

import co.unlearning.aicareer.domain.recruitmenttype.RecruitmentType;
import co.unlearning.aicareer.domain.recruitmenttype.dto.RecruitmentTypeResponseDto;
import co.unlearning.aicareer.domain.sitemap.SiteMap;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
            return SiteMapInfo.builder()
                    .url(siteMap.getUrl())
                    .lastModified(LocalDateTimeStringConverter.LocalDateTimeToString(siteMap.getLastModified()))
                    .build();
        }
        public static List<SiteMapInfo> of(List<SiteMap> siteMaps) {
            return siteMaps.stream().map(SiteMapInfo::of).collect(Collectors.toList());
        }
    }
}
