package co.unlearning.aicareer.domain.Image.service;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.domain.Image.dto.ImageRequirementDto;
import co.unlearning.aicareer.domain.Image.repository.ImageRepository;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import co.unlearning.aicareer.global.utils.validator.ImageValidator;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    public Image addOneImage(ImageRequirementDto.ImagePost imagePost) throws IOException {
        ImageValidator.ImageExistValidator(imagePost.getImageFile());
        MultipartFile multipartFile = imagePost.getImageFile();
        String originImageName = imagePost.getOriginImageName();
        String contentType;
        if(Objects.equals(multipartFile.getContentType(), "image/jpeg")) {
            contentType = ".jpg";
        } else if (Objects.equals(multipartFile.getContentType(), "image/png")) {
            contentType = ".png";
        } else {
            throw new BusinessException(ResponseErrorCode.INVALID_IMAGE_CONTENT_TYPE);
        }
        //window 구별
        String osPath = getOsPath();
        String imagePath = originImageName + UUID.randomUUID() + contentType;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String formattedDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter)+'/';

        String absolutePath = Paths.get("").toAbsolutePath() +osPath;

        File file = new File(absolutePath + imagePath);
        if(!file.exists()) {
            file.mkdirs();
        }
        multipartFile.transferTo(file);
        Image image = Image.builder()
                .absolutePath(absolutePath)
                .imageUrl(imagePath)
                .createdDate(LocalDateTime.now())
                .build();
        return imageRepository.save(image);
    }
    public List<Image> addAllImage(List<ImageRequirementDto.ImagePost> imagePosts) throws IOException{
        List<Image> imageList = new ArrayList<>();
        for(ImageRequirementDto.ImagePost imagePost : imagePosts) {
            imageList.add(addOneImage(imagePost));
        }
        return imageList;
    }
    public Image getImageByUrl(String url) {
        return imageRepository.findByImageUrl(url).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.INVALID_IMAGE_URL)
        );
    }
    public void deleteImage(String url) {
        Image image = getImageByUrl(url);
        File file = new File(image.getAbsolutePath()+image.getImageUrl());
        String osPath = getOsPath();
        try {
            if (file.exists()) {
                file.delete();
            }
        }catch (Exception e) {
            log.info(e.getMessage());
            throw new BusinessException(ResponseErrorCode.NOT_FOUND_IMAGE_FILE);
        }
        imageRepository.delete(image);
    }

    private static String getOsPath() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return "\\image\\";
        }else {
            return "/image/";
        }
    }


    public Image addS3Image(ImageRequirementDto.ImagePost imagePost) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imagePost.getImageFile().getInputStream().available());
        amazonS3.putObject(bucket,imagePost.getOriginImageName(),imagePost.getImageFile().getInputStream(),objectMetadata);
        return new Image();
    }
}
