package com.example.cafe24test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @GetMapping("/")
    public void getRes() {
        service.printList();
    }
    @GetMapping("/5")
    public ResponseEntity<Void> ok() {
        return ResponseEntity.ok().build();
    }
    @GetMapping("/6")
    public ResponseEntity<String> bad() {
        return ResponseEntity.badRequest().build();
    }

}
