package com.example.product_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class S3TestService {

    private final AmazonS3 amazonS3;

    public S3TestService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // Bucket listesini getir
    public void listBuckets() {
        List<Bucket> buckets = amazonS3.listBuckets();
        System.out.println("AWS S3 Buckets:");
        buckets.forEach(bucket -> System.out.println(bucket.getName()));
    }
}
