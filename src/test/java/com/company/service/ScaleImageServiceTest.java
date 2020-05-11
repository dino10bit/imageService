package com.company.service;

import com.amazonaws.services.s3.model.S3Object;
import com.company.model.images.Image;
import com.company.service.aws.AwsS3Service;
import com.company.service.image.ScaleImageService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScaleImageServiceTest {

    private ScaleImageService scaleImageService;
    @Mock private AwsS3Service awsS3Service;

    @Before
    public void setup(){
        scaleImageService = new ScaleImageService(awsS3Service, "/sourceRoot");
    }

    @Test
    public void getScaledImage_returns_image() throws IOException {
        String filename = "filename";
        S3Object s3Object = new S3Object();
        InputStream testInputStream = IOUtils.toInputStream("some test data for my input stream", "UTF-8");
        s3Object.setObjectContent(testInputStream);

        when(awsS3Service.getPathOfObjectInBucket(anyString(), anyString())).thenReturn("not_empty_path");
        when(awsS3Service.downloadFile(anyString())).thenReturn(s3Object);

        Image image = scaleImageService.process("thumbnail", filename);

        assertThat(s3Object.getObjectContent()).isEqualTo(image.getImageContent());
    }
}
