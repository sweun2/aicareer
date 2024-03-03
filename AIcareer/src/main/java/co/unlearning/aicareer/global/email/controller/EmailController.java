package co.unlearning.aicareer.global.email.controller;

import co.unlearning.aicareer.global.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/email")
@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
	// 임시 비밀번호 발급
	// 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @PostMapping("/")
    public ResponseEntity<Void> sendEmail(){
        emailService.sendMail();
        return ResponseEntity.ok().build();
    }
}