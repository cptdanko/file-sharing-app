package com.mydaytodo.sfa.asset.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("ping", HttpStatus.OK);
    }
    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return new ResponseEntity<>("great success!!!", HttpStatus.OK);
    }
}
