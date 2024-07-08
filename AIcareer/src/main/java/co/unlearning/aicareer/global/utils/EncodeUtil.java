package co.unlearning.aicareer.global.utils;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncodeUtil {

    @Value("${channel-talk.api-key}")
    private String secretKey;

    private static String staticSecretKey;

    @PostConstruct
    private void init() {
        staticSecretKey = this.secretKey;
    }

    public static String encodeWithHmacSHA256(String memberId) {
        String algorithm = "HmacSHA256";

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(hexify(staticSecretKey), algorithm));

            byte[] hash = mac.doFinal(memberId.getBytes());

            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

             return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] hexify(String string) {
        return Base64.getDecoder().decode(string);
    }
}

