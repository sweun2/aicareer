package co.unlearning.aicareer.domain.Image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


public class ImageRequirementDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImagePost {
        @Schema(description = "원본 이미지 파일 이름. 확장자를 제외하고 보내주세요. ")
        private String originImageName;
        @Schema(description = "원본 이미지 파일")
        private MultipartFile imageFile;
    }

}
