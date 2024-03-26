package co.unlearning.aicareer.global.email.controller;

import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.global.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/email")
@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailController {
    private final EmailService emailService;
    private final UserService userService;
    @GetMapping("/")
    public ResponseEntity<String> sendEmail(){
        userService.checkAdmin();
        emailService.sendMail();
        return ResponseEntity.ok().body("test");
    }
}