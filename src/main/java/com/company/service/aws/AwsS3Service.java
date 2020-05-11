package com.company.service.aws;

import com.amazonaws.services.s3.model.S3Object;
import com.company.model.images.predefinedTypes.PredefinedImage;

import java.io.File;

public interface AwsS3Service {

    String getPathOfObjectInBucket(String imageType, String filename);
    S3Object downloadFile(String keyName);
    void uploadFile(String filename, File file);
    boolean removeImage(PredefinedImage predefinedImageType, String relativeFileLocation);
}
