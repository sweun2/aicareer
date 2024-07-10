package co.unlearning.aicareer.domain.common.Image.service;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.dto.ImageRequirementDto;
import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.ImageValidator;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public Image getImageByUrl(String url) {
        return imageRepository.findByImageUrl(url).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
        );
    }
    public Image addBase64Image(String base64Image) throws IOException {
        // Base64 문자열에서 이미지 데이터 추출
        String[] parts = base64Image.split(",");
        String imageString = parts[1];
        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        // 파일 확장자 결정
        String contentType = parts[0].split(":")[1].split(";")[0];
        String fileExtension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new BusinessException(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE);
        };

        // 파일 경로 및 이름 설정
        String imagePath = (LocalDateTimeStringConverter.LocalDateTimeToString(LocalDateTime.now()) + UUID.randomUUID().toString() + fileExtension).replaceAll(" ", "");

        // 파일 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageBytes.length);
        objectMetadata.setContentType(contentType);

        // 파일 업로드
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, imagePath, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicReadWrite)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 업로드 중 에러가 발생하였습니다.");
        }

        // 업로드된 파일의 URL 구성
        String imageUrl = amazonS3.getUrl(bucket, imagePath).toString();

        Image image = Image.builder()
                .imageUrl(ImagePathLengthConverter.slicingImagePathLength(imageUrl))
                .createdDate(LocalDateTime.now())
                .isRelated(false)
                .build();

        return imageRepository.save(image);
    }
    public Image addS3Image(ImageRequirementDto.ImagePost imagePost) throws IOException {
        ImageValidator.ImageExistValidator(imagePost.getImageFile());
        MultipartFile multipartFile = imagePost.getImageFile();
        String contentType;
        if (Objects.equals(multipartFile.getContentType(), "image/jpeg")) {
            contentType = ".jpg";
        } else if (Objects.equals(multipartFile.getContentType(), "image/png")) {
            contentType = ".png";
        } else if (Objects.equals(multipartFile.getContentType(), "image/webp")) {
            contentType = ".webp";
        } else {
            throw new BusinessException(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE);
        }
        String imagePath = (LocalDateTimeStringConverter.LocalDateTimeToString(LocalDateTime.now()) + UUID.randomUUID().toString() + contentType).replaceAll(" ", "");

        // 파일 변환
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imagePost.getImageFile().getSize());
        objectMetadata.setContentType(imagePost.getImageFile().getContentType());

        // 파일 업로드
        try (InputStream inputStream = imagePost.getImageFile().getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, imagePath, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicReadWrite)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생하였습니다. (%s)", imagePost.getImageFile().getOriginalFilename()));
        }

        // 업로드된 파일의 URL 구성
        String imageUrl = amazonS3.getUrl(bucket, imagePath).toString();

        Image image = Image.builder()
                .imageUrl(ImagePathLengthConverter.slicingImagePathLength(imageUrl))
                .createdDate(LocalDateTime.now())
                .isRelated(false)
                .build();

        return imageRepository.save(image);
    }

    public void deleteImageByUrl(String imageUrl) {
        Image image = getImageByUrl(imageUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, imageUrl));
        imageRepository.delete(image);
    }
    private void validateS3ImageExists(String fileName) throws FileNotFoundException {
        if(!amazonS3.doesObjectExist(bucket, fileName))
            throw new FileNotFoundException();
    }
    public InputStream downloadS3Image(String fileName) throws FileNotFoundException {
        validateS3ImageExists(fileName);

        S3Object s3Object = amazonS3.getObject(bucket, fileName);
        return s3Object.getObjectContent();
    }

    public String processBase64Images(String content, Consumer<Image> imageConsumer) {
        // 정규식을 사용하여 base64 이미지 문자열을 찾습니다.
        Pattern pattern = Pattern.compile("data:image/(png|jpeg|webp);base64,([a-zA-Z0-9+/=]+)");
        Matcher matcher = pattern.matcher(content);
        StringBuilder processedContent = new StringBuilder();
        // 모든 base64 이미지 문자열을 찾고 처리합니다.
        while (matcher.find()) {
            String base64Image = matcher.group(0);
            try {
                // base64 문자열에서 불필요한 텍스트를 제거합니다.
                String cleanedBase64Image = cleanBase64String(base64Image);
                log.info("cleanedBase64Image : {}", cleanedBase64Image);
                // base64 문자열의 유효성을 검증합니다.
                if (isValidBase64(cleanedBase64Image)) {
                    // base64 문자열에서 이미지를 추가하고 URL을 가져옵니다.
                    Image image = addBase64Image(cleanedBase64Image);
                    String imageUrl = image.getImageUrl();
                    // base64 문자열을 이미지 URL로 교체합니다.
                    matcher.appendReplacement(processedContent, imageUrl);
                } else {
                    // 유효하지 않은 base64 문자열은 그대로 유지합니다.
                    matcher.appendReplacement(processedContent, base64Image);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 오류 발생 시 그대로 base64 문자열을 사용합니다.
                matcher.appendReplacement(processedContent, base64Image);
            }
        }
        matcher.appendTail(processedContent);

        return processedContent.toString();
    }

    private String cleanBase64String(String base64Image) {
        // base64 문자열에서 "data:image/..." 부분을 제거하고 base64 데이터만 추출합니다.
        String[] parts = base64Image.split(",");
        if (parts.length > 1) {
            // 불필요한 텍스트를 제거합니다.
            parts[1] = parts[1].replaceAll("[^a-zA-Z0-9+/=]", "");
            return parts[0] + "," + parts[1];
        }
        return base64Image;
    }

        private boolean isValidBase64(String base64) {
            try {
                // base64 문자열에서 헤더를 제거합니다.
                String[] parts = base64.split(",");
                String imageString = parts[1];
                // base64 문자열을 디코딩하여 유효성을 검증합니다.
                Base64.getDecoder().decode(imageString);
                return true;
            } catch (IllegalArgumentException e) {
                // 디코딩 중 오류가 발생하면 유효하지 않은 base64 문자열로 간주합니다.
                return false;
            }
        }

}
