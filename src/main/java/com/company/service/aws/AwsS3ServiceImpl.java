package com.company.service.aws;

import com.company.model.images.predefinedTypes.PredefinedImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.exception.NotFoundException;
import static com.company.utils.FileUtils.getImagePath;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    @Value("${aws.s3.endpoint}")
    private String endpointUrl;
    @Value("${aws.bucket.name}")
    private String bucketName;
    @Value("${aws.secretkey}")
    private String secretKey;
    @Value("${aws.accesskey}")
    private String accessKey;

    private AmazonS3 amazonS3;

    private final Logger LOGGER = LoggerFactory.getLogger(AwsS3ServiceImpl.class);

    @Inject
    public AwsS3ServiceImpl(
            @Value("${aws.s3.endpoint}") String endpointUrl,
            @Value("${aws.bucket.name}") String bucketName,
            @Value("${aws.accesskey}") String accessKey,
            @Value("${aws.secretkey}") String secretKey,
            AmazonS3 amazonS3
    ) {
        this.endpointUrl = endpointUrl;
        this.bucketName = bucketName;
        this.secretKey = secretKey;
        this.accessKey = accessKey;
        this.amazonS3 = amazonS3;
    }

    @PostConstruct
    private void initialize() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
            this.amazonS3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new NotFoundException("Invalid amazon credentials.");
        }
    }

    @Override
    public String getPathOfObjectInBucket(String imageType, String filename) {
        String imagePath = getImagePath(imageType, filename);

        try {
            if (amazonS3.doesObjectExist(bucketName, imagePath)) {
                return imagePath;
            }
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
            throw new NotFoundException("AWS S3 object " + filename + " is not exists in the bucket");
        }

        return "";
    }

    @Override
    public S3Object downloadFile(String filePath) {
        S3Object s3object = new S3Object();

        try {
            s3object = this.amazonS3.getObject(new GetObjectRequest(bucketName, filePath));
        } catch (AmazonServiceException e) {
            LOGGER.info("Error Message:    " + e.getMessage());
            LOGGER.info("HTTP Status Code: " + e.getStatusCode());
            LOGGER.info("AWS Error Code:   " + e.getErrorCode());
        } catch (AmazonClientException ace) {
            LOGGER.info("Error Message: " + ace.getMessage());
        }

        return s3object;
    }

    public boolean removeImage(PredefinedImage predefinedImageType, String relativeFileLocation) {
        return true; // TODO: complete it
    }

    @Override
    public void uploadFile(String filename, File file) {
        this.amazonS3.putObject(bucketName, filename, file);
    }
}