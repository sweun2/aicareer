package co.unlearning.aicareer.global.utils.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class ImagePathLengthConverter {
    public static String serverUrl;
    public static String s3Url;
    @Value("${back-url}")
    public void setServerUrl(String serverUrl) {
        ImagePathLengthConverter.serverUrl = serverUrl;
    }
    @Value("${s3-url}")
    public void setS3Url(String s3Url) {
        ImagePathLengthConverter.s3Url = s3Url;
    }
    public static String slicingImagePathLength(String imagePath) {
        int len = ("https://"+s3Url+"/").length();
        return imagePath.substring(len);
    }
    public static String encodeURI(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static String extendImagePathLength(String imagePath) {
        return "https://"+s3Url+"/"+imagePath;
    }
}
