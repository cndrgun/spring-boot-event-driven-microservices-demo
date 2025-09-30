package com.example.product_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file) {
        try {
            //Dosya tipi kontrolü (MIME type)
            String contentType = file.getContentType();
            if (contentType == null ||
                    !(contentType.equals("image/png") ||
                            contentType.equals("image/jpeg") ||
                            contentType.equals("image/jpg") ||
                            contentType.equals("image/gif"))) {
                throw new RuntimeException("Only image files are allowed (PNG, JPG, JPEG, GIF)");
            }

            //Dosya uzantısı kontrolü (isteğe bağlı ama ekstra güvenlik)
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null ||
                    !(originalFilename.endsWith(".png") ||
                            originalFilename.endsWith(".jpg") ||
                            originalFilename.endsWith(".jpeg") ||
                            originalFilename.endsWith(".gif"))) {
                throw new RuntimeException("Invalid file extension. Only PNG, JPG, JPEG, GIF allowed");
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

            return amazonS3.getUrl(bucketName, fileName).toString();

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }


}
