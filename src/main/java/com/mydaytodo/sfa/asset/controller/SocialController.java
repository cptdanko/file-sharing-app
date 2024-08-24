package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.model.EmailRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.FileServiceImpl;
import com.mydaytodo.sfa.asset.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/social")
@Tag(name = "Social controller")
@Slf4j
public class SocialController {

    @Autowired
    private FileServiceImpl fileService;

    @Autowired
    private MailService mailService;

    @PostMapping(value = "/sendMail")
    public ResponseEntity<ServiceResponse> sendMail(@RequestBody EmailRequest emailRequest) {
        log.info(emailRequest.toString());
        if(fileService.validateFileType(emailRequest.getFilesToAttach()).getStatus() != null) {
            ServiceResponse response = fileService.validateFileType(emailRequest.getFilesToAttach());
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        }
        ServiceResponse serviceResponse = mailService.sendEmail(emailRequest);
        return new ResponseEntity<>(serviceResponse, HttpStatus.valueOf(serviceResponse.getStatus()));
    }
}
