package co.unlearning.aicareer.domain.Image.controller;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.dto.ImageResponseDto;
import co.unlearning.aicareer.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@Slf4j
@Tag(name = "image", description = "이미지 api")
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final UserService userService;
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "단일 이미지 파일 올리기", description = "이미지 파일을 저장합니다.")
    @ApiResponse(
            responseCode = "201",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = ImageResponseDto.ImageData.class)))
    @PostMapping("/")
    public ResponseEntity<ImageResponseDto.ImageData> postImage(@RequestBody MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ImageResponseDto.ImageData.of(new Image()));
    }
    @Operation(summary = "이미지 파일 다운로드", description = "이미지 파일 다운로드하기")
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    schema = @Schema(implementation = ImageResponseDto.ImageData.class)))
    @GetMapping("/{url}")
    public ResponseEntity<Resource> downloadAttach(@Parameter(name = "url", description = "이미지 url", in = ParameterIn.PATH)
                                                       @PathVariable String url) throws MalformedURLException {
        //...itemId 이용해서 고객이 업로드한 파일 이름인 uploadFileName랑 서버 내부에서 사용하는 파일 이름인 storeFileName을 얻는다는 내용은 생략


        //UrlResource resource = new UrlResource("file:/home/ubuntu/img/"+filePath);

/*        //한글 파일 이름이나 특수 문자의 경우 깨질 수 있으니 인코딩 한번 해주기
        String encodedUploadFileName = UriUtils.encode(uploadFileName,
                StandardCharsets.UTF_8);*/

        //아래 문자를 ResponseHeader에 넣어줘야 한다. 그래야 링크를 눌렀을 때 다운이 된다.
        //정해진 규칙이다.
        String contentDisposition = "attachment; filename=\"" + "file:/home/ubuntu/img/" ;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(new UrlResource(""));
    }
}