package co.unlearning.aicareer.domain.sitemap.controller;

import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentRequirementDto;
import co.unlearning.aicareer.domain.recruitment.dto.RecruitmentResponseDto;
import co.unlearning.aicareer.domain.sitemap.dto.SiteMapResponseDto;
import co.unlearning.aicareer.domain.sitemap.service.SiteMapService;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "sitemap", description = "site map api")
@RequiredArgsConstructor
@RequestMapping("/api/sitemap")
public class SiteMapController {
    private final SiteMapService siteMapService;
    @Operation(summary = "모든 url/lastmodified 반환", description = "모든 url/lastmodified를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = SiteMapResponseDto.SiteMapInfo.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
    })
    @PostMapping("/all")
    public ResponseEntity<List<SiteMapResponseDto.SiteMapInfo>> getAllSiteMapInfo() {
        return ResponseEntity.ok(SiteMapResponseDto.SiteMapInfo.of(siteMapService.findAllSiteMap()));
    }
    @Operation(summary = "1년내 modified된 sitemap들만 반환, 주로 이거 쓰면 될거 같아요", description = "1년 내의 url/lastmodified를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = SiteMapResponseDto.SiteMapInfo.class))))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.UID_NOT_FOUND),
    })
    @PostMapping("/all")
    public ResponseEntity<List<SiteMapResponseDto.SiteMapInfo>> getSiteMapsLastModifiedWithinOneYear() {
        return ResponseEntity.ok(SiteMapResponseDto.SiteMapInfo.of(siteMapService.findSiteMapsLastModifiedWithinOneYear()));
    }
}
