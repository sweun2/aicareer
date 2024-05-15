package co.unlearning.aicareer.domain.common.Image.service;

import co.unlearning.aicareer.domain.common.Image.Image;
import co.unlearning.aicareer.domain.common.Image.dto.ImageRequirementDto;
import co.unlearning.aicareer.domain.common.Image.repository.ImageRepository;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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
    public Image addS3Image(ImageRequirementDto.ImagePost imagePost) throws IOException {
        ImageValidator.ImageExistValidator(imagePost.getImageFile());
        MultipartFile multipartFile = imagePost.getImageFile();
        String contentType;
        if(Objects.equals(multipartFile.getContentType(), "image/jpeg")) {
            contentType = ".jpg";
        } else if (Objects.equals(multipartFile.getContentType(), "image/png")) {
            contentType = ".png";
        } else if (Objects.equals(multipartFile.getContentType(),"image/webp")) {
            contentType = ".webp";
        }
        else {
            throw new BusinessException(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE);
        }
        String imagePath = (UUID.randomUUID().toString() + UUID.randomUUID().toString() + contentType).replaceAll(" ", "");

        //파일 변환
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imagePost.getImageFile().getSize());
        objectMetadata.setContentType(imagePost.getImageFile().getContentType());

        //파일 업로드
        try(InputStream inputStream = imagePost.getImageFile().getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, imagePath, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicReadWrite)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생하였습니다. (%s)", imagePost.getImageFile().getOriginalFilename()));
        }

        Image image = Image.builder()
                .imageUrl(imagePath)
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
    public void deleteS3ImageUrl(String imageUrl) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, imageUrl));
    }
    public String getS3ImageUrl(String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }
    private void validateS3ImageExists(String fileName) throws FileNotFoundException {
        if(!amazonS3.doesObjectExist(bucket, fileName))
            throw new FileNotFoundException();
    }
    public byte[] downloadS3Image(String fileName) throws FileNotFoundException {
        validateS3ImageExists(fileName);

        S3Object s3Object = amazonS3.getObject(bucket, fileName);
        S3ObjectInputStream s3ObjectContent = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(s3ObjectContent);
        }catch (IOException e ){
            throw new BusinessException(ResponseErrorCode.NOT_FOUND_IMAGE_FILE);
        }
    }
}
