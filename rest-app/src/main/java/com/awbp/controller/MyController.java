package com.awbp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MyController {

    @GetMapping
    public ResponseEntity<String> start() {
        return ResponseEntity.ok("Привет из REST!!!");
    }
}
