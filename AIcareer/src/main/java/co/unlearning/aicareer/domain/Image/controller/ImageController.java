package co.unlearning.aicareer.domain.Image.controller;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.dto.ImageRequirementDto;
import co.unlearning.aicareer.domain.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.domain.Image.service.ImageService;
import co.unlearning.aicareer.domain.user.service.UserService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Tag(name = "image", description = "이미지 api")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final UserService userService;
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "단일 이미지 파일 올리기", description = "딘일 이미지 파일을 저장합니다.")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = ImageResponseDto.ImageData.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED),
            @ApiErrorCodeExample(ResponseErrorCode.NOT_FOUND_IMAGE_FILE),
    })
    @PostMapping(value = "/one",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageResponseDto.ImageData> postOneImage(ImageRequirementDto.ImagePost imagePost) throws IOException {
        // 메소드 구현
        userService.checkAdmin();
        return ResponseEntity.status(HttpStatus.CREATED).body(ImageResponseDto.ImageData.of(imageService.addOneImage(imagePost)));
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "다중 이미지 파일 올리기", description = "여러 이미지 파일을 저장합니다.")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ImageResponseDto.ImageData.class))
            )
    )
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE),
            @ApiErrorCodeExample(ResponseErrorCode.NOT_FOUND_IMAGE_FILE),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED),
    })
    @PostMapping(value = "/all",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImageResponseDto.ImageData>> postAllImage(List<ImageRequirementDto.ImagePost> imagePosts) throws IOException {
        userService.checkAdmin();
        return ResponseEntity.status(HttpStatus.CREATED).body(ImageResponseDto.ImageData.of(imageService.addAllImage(imagePosts)));

    }

    @Operation(summary = "이미지 파일 다운로드", description = "이미지 파일 다운로드하기,imageUrl을 입력하세요")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = ImageResponseDto.ImageData.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
    })
    @GetMapping("/{url}")
    public ResponseEntity<Resource> downloadAttach(@Parameter(name = "url", description = "이미지 url", in = ParameterIn.PATH)
                                                   @PathVariable("url") String url) throws MalformedURLException {
        Image image = imageService.getImageByUrl(url);
        UrlResource resource = new UrlResource("file:" +image.getAbsolutePath()+ image.getImageUrl());
        //한글 파일 이름이나 특수 문자의 경우 깨질 수 있으니 인코딩 한번 해주기

        String encodedUploadFileName = UriUtils.encode(image.getImageUrl(),
                StandardCharsets.UTF_8);

        //아래 문자를 ResponseHeader에 넣어줘야 한다. 그래야 링크를 눌렀을 때 다운이 된다.
        //정해진 규칙이다.
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "이미지 파일 제거하기", description = "이미지 파일을 제거합니다.")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = ImageResponseDto.ImageData.class)))
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(ResponseErrorCode.INTERNAL_SERVER_ERROR),
            @ApiErrorCodeExample(ResponseErrorCode.NOT_FOUND_IMAGE_FILE),
            @ApiErrorCodeExample(ResponseErrorCode.INVALID_IMAGE_URL),
            @ApiErrorCodeExample(ResponseErrorCode.USER_NOT_ALLOWED),
    })
    @DeleteMapping(value = "/delete/{url}")
    public ResponseEntity<Void> deleteImage(@Parameter(name = "url", description = "이미지 url", in = ParameterIn.PATH)
                                                 @PathVariable String url) throws IOException {
        userService.checkAdmin();
        imageService.deleteImage(url);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}