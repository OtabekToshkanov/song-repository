package com.microservice.resource_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.microservice.resource_service.excpetion.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;
    private final String bucketName;

    public S3Service(AmazonS3 amazonS3,
                     @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String uploadFile(byte[] fileData, String contentType) {
        String key = String.format("mp3/%s", UUID.randomUUID());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileData.length);
            metadata.setContentType(contentType);

            amazonS3.putObject(new PutObjectRequest(bucketName, key, new ByteArrayInputStream(fileData), metadata));

            return key;
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to upload file to S3");
        }
    }

    public byte[] downloadFile(String s3Key) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, s3Key);
            return IOUtils.toByteArray(s3Object.getObjectContent());
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to download file from S3");
        }
    }

    public void deleteFile(String s3Key) {
        try {
            amazonS3.deleteObject(bucketName, s3Key);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to delete file from S3");
        }
    }
}
