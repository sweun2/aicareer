package co.unlearning.aicareer.domain.Image.dto;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.global.utils.converter.ImagePathLengthConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class ImageResponseDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {
        @Schema(description = "이미지 url")
        private String imageUrl;
        public static ImageData of(Image image) {
            return ImageData.builder()
                    .imageUrl(ImagePathLengthConverter.extendImagePathLength(image.getImageUrl()))
                    .build();
        }

        public static List<ImageData> of(List<Image> images) {
            return images.stream().map(ImageData::of).collect(Collectors.toList());
        }
    }
}
