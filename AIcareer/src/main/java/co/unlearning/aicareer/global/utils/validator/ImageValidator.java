package co.unlearning.aicareer.global.utils.validator;

import co.unlearning.aicareer.global.utils.error.code.ImageErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator {
    public static void ImageExistValidator(MultipartFile multipartFile) {
        if(multipartFile.isEmpty())
            throw new BusinessException(ImageErrorCode.NOT_FOUND_IMAGE_FILE);
    }
}
