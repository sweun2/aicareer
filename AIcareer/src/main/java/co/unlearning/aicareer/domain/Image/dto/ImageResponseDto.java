package co.unlearning.aicareer.domain.Image.dto;

import co.unlearning.aicareer.domain.Image.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
        @Schema(description = "원본 이미지 파일 이름")
        private String absolutePath;
        public static ImageData of(Image image) {
            return ImageData.builder()
                    .imageUrl(image.getImageUrl())
                    .absolutePath(image.getAbsolutePath())
                    .build();
        }

        public static List<ImageData> of(List<Image> images) {
            return images.stream().map(ImageData::of).collect(Collectors.toList());
        }
    }
}
