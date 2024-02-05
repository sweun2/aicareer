package co.unlearning.aicareer.global.utils.converter;

import co.unlearning.aicareer.domain.Image.Image;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImagePathLengthConverter {
    public static String serverUrl;

    @Value("${back-url}")
    public void setServerUrl(String serverUrl) {
        ImagePathLengthConverter.serverUrl = serverUrl;
    }
    public static String slicingImagePathLength( String imagePath) {
        int len = (serverUrl+"/api/image/").length();
        return imagePath.substring(len);
    }
    public static String extendImagePathLength(String imagePath) {
        return serverUrl+"/api/image/"+imagePath;
    }
}
