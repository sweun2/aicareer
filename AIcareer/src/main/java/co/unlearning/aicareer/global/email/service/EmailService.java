package co.unlearning.aicareer.global.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
/*    public void sendMail(){
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("unlearningdev@gmail.com");
            messageHelper.setTo("sweun3@gmail.com");
            messageHelper.setSubject("Email subject");
            messageHelper.setText("<p>Email body</p>", true);
        });
    }*/
    public void sendMail() {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo("sweun3@gmail.com");
            msg.setSubject("test email");
            msg.setText("test");
            javaMailSender.send(msg);
        }
        catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}