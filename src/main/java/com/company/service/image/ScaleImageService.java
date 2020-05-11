package com.company.service.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.company.service.aws.AwsS3Service;
import com.company.model.images.Image;
import com.company.model.images.predefinedTypes.PredefinedImage;
import com.company.model.images.predefinedTypes.PredefinedImageFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import static com.company.utils.FileUtils.getFileFromImage;

@Service
public class ScaleImageService {
    private final String ORIGINAL_IMAGE_TYPE = "original";
    private final String sourceRootUrl;

    private Image image;
    private final AwsS3Service awsS3Service;
    private final ScaleImageHelper scaleImageHelper;


    @Inject
    public ScaleImageService(
            AwsS3Service awsS3Service,
            @Value("${source-root-url}") String sourceRootUrl
    ) {
        this.sourceRootUrl = sourceRootUrl;
        this.awsS3Service = awsS3Service;
        this.scaleImageHelper = new ScaleImageHelper();
    }

    public Image process(
            String predefinedTypeName,
            String filename
    ) throws IOException {
        String awsS3fileBucketPath = awsS3Service.getPathOfObjectInBucket(predefinedTypeName, filename);

        if (awsS3fileBucketPath.isEmpty()) {
            sendImage(predefinedTypeName, filename);
        } else {
            image = new Image(awsS3Service.downloadFile(awsS3fileBucketPath).getObjectContent());
        }

        return image;
    }

    private void sendImage(
            String predefinedTypeName,
            String filename
    ) throws IOException {
        PredefinedImage predefinedImage = PredefinedImageFactory.getPredefinedImage(predefinedTypeName);

        String filePath = awsS3Service.getPathOfObjectInBucket(ORIGINAL_IMAGE_TYPE, filename);

        if (filePath.isEmpty()) {
            sendOriginalImageToAws(filePath);
            sendImage(predefinedTypeName, filename);
        } else {
            optimizeAndSendToAws(predefinedImage, filePath, predefinedTypeName);
            process(predefinedTypeName, filename);
        }
    }

    private void sendOriginalImageToAws(String filePath) {
        File file =  new File(sourceRootUrl + filePath);
        awsS3Service.uploadFile(filePath, file);
    }

    private void optimizeAndSendToAws(
            PredefinedImage predefinedImage,
            String filePath,
            String predefinedTypeName
    ) throws IOException {
        Image originalImage = new Image(awsS3Service.downloadFile(filePath).getObjectContent());
        InputStream scaledImageStream = this.scaleImageHelper.process(originalImage, predefinedImage);
        originalImage.setImageContent(scaledImageStream);

        File fileToUpload = getFileFromImage(originalImage, filePath);
        String editedImageFilePath = filePath.replace(ORIGINAL_IMAGE_TYPE, predefinedTypeName);

        awsS3Service.uploadFile(editedImageFilePath, fileToUpload);
    }
}
