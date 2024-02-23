package co.unlearning.aicareer.jobpost.domain.Image.controller;

import co.unlearning.aicareer.jobpost.domain.Image.Image;
import co.unlearning.aicareer.jobpost.domain.Image.dto.ImageRequirementDto;
import co.unlearning.aicareer.jobpost.domain.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.jobpost.domain.Image.service.ImageService;
import co.unlearning.aicareer.jobpost.domain.recruitment.service.RecruitmentBatchService;
import co.unlearning.aicareer.jobpost.domain.user.service.UserService;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "image", description = "이미지 api")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final UserService userService;
    private final RecruitmentBatchService recruitmentBatchService;
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/one",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponseDto.ImageData> postS3Image(ImageRequirementDto.ImagePost imagePost) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ImageResponseDto.ImageData.of(imageService.addS3Image(imagePost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(
            value="/{url}",
            produces={MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public ResponseEntity<byte[]> downloadS3Attach(@Parameter(name = "url", description = "이미지 url", in = ParameterIn.PATH)
                                                   @PathVariable("url") String url) throws MalformedURLException, FileNotFoundException {

        return ResponseEntity.ok()
                .cacheControl(
                        CacheControl.maxAge(10, TimeUnit.MINUTES)
                                .mustRevalidate()
                                .cachePrivate()
                )
                .body(imageService.downloadS3Image(url));
    }
}