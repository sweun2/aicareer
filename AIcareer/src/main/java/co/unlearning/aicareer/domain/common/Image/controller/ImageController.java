package co.unlearning.aicareer.domain.common.Image.controller;

import co.unlearning.aicareer.domain.common.Image.dto.ImageRequirementDto;
import co.unlearning.aicareer.domain.common.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.domain.common.Image.service.ImageService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.service.RecruitmentBatchService;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "image", description = "이미지 api")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/one",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponseDto.ImageData> postS3Image(ImageRequirementDto.ImagePost imagePost) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ImageResponseDto.ImageData.of(imageService.addS3Image(imagePost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(
            value = "/{url}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE}
    )
    public ResponseEntity<StreamingResponseBody> downloadS3Attach(
            @Parameter(name = "url", description = "이미지 url", in = ParameterIn.PATH)
            @PathVariable("url") String url) {
        log.info("url: {}", url);
        StreamingResponseBody stream = out -> {
            try (InputStream inputStream = imageService.downloadS3Image(ImagePathLengthConverter.slicingImagePathLength(url))) {
                byte[] buffer = new byte[8192]; // 8KB 버퍼
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new BusinessException(ResponseErrorCode.NOT_FOUND_IMAGE_FILE);
            }
        };

        // Content-Type 설정
        String contentType = url.endsWith(".png") ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(
                        CacheControl.maxAge(10, TimeUnit.MINUTES)
                                .mustRevalidate()
                                .cachePrivate()
                )
                .body(stream);
    }
}