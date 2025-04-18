package co.unlearning.aicareer.global.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import static co.unlearning.aicareer.global.utils.ImageUtil.getExtension;
import static co.unlearning.aicareer.global.utils.ImageUtil.getMimeType;

public class MultipartFileUtil {

    public static MultipartFile convertUrlToMultipartFile(String url) throws IOException, TranscoderException, TranscoderException {
        URL imageUrl = new URL(url);
        String fileName = imageUrl.getPath().substring(imageUrl.getPath().lastIndexOf('/') + 1);
        String fileType = getMimeType(fileName);
        String fileExtension = getExtension(fileName);

        if ("svg".equalsIgnoreCase(fileExtension)) {
            // SVG 파일을 PNG로 변환
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(imageUrl.toString());

            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(ostream);

            t.transcode(input, output);

            byte[] imageBytes = ostream.toByteArray();
            InputStream is = new ByteArrayInputStream(imageBytes);

            // MockMultipartFile 생성
            return new MockMultipartFile("file", fileName.replace(".svg", ".png"), "image/png", is);
        } else {
            // 기타 형식의 이미지 처리
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            if (bufferedImage == null) {
                throw new IOException("Image could not be read from URL: " + url);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, fileExtension, baos);

            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            return new MockMultipartFile("file", fileName, fileType, is);
        }
    }
    public static MultipartFile convertImageToSupportedFormat(MultipartFile file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .scale(1.0)  // scale을 1.0으로 설정하여 원본 크기를 유지
                    .outputFormat("jpg")
                    .toOutputStream(baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new MockMultipartFile(
                    file.getName(),
                    file.getOriginalFilename().replace("." + ImageUtil.getExtension(file.getOriginalFilename()), ".jpg"),
                    "image/jpeg",
                    bais
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert file", e);
        }
    }
    public static String convertToBase64(MultipartFile file) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .scale(1.0) // 이미지 크기를 유지하면서 변환
                    .outputFormat("jpg")
                    .toOutputStream(baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeBase64String(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert file to Base64", e);
        }
    }
}
