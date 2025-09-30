package com.example.product_service.controller;

import com.example.product_service.service.S3TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3TestController {

    private final S3TestService s3TestService;

    public S3TestController(S3TestService s3TestService) {
        this.s3TestService = s3TestService;
    }

    @GetMapping("/test-s3")
    public String testS3() {
        s3TestService.listBuckets();
        return "Check console for bucket list!";
    }
}
