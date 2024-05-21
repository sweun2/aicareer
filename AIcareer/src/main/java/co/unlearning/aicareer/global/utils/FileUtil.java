package co.unlearning.aicareer.global.utils;

import java.util.HashMap;
import java.util.Map;

public class FileUtil {

    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("webp","image/webp");
        mimeTypes.put("svg","image/svg+xml");
        // 필요한 경우 더 많은 확장자를 추가할 수 있습니다.
    }

    public static String getMimeType(String fileName) {
        String extension = getExtension(fileName).toLowerCase();
        return mimeTypes.getOrDefault(extension, "application/octet-stream");
    }

    public static String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
