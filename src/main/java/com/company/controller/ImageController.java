package com.company.controller;

import com.company.model.images.Image;
import com.company.service.aws.AwsS3Service;
import com.company.service.aws.AwsS3ServiceImpl;
import com.company.service.file.FileValidatorService;
import com.company.service.image.ScaleImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import com.company.model.images.predefinedTypes.PredefinedImage;

import javax.inject.Inject;
import java.util.Optional;

@RestController
@RequestMapping("/image")
public class ImageController {
    private ScaleImageService scaleImageService;
    private AwsS3Service awsS3Service;
    private String sourceRootUrl;
    private FileValidatorService fileValidatorService;
    private final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    @Inject
    public ImageController(
            FileValidatorService fileValidatorService,
            ScaleImageService scaleImageService,
            AwsS3ServiceImpl awsS3Service,
            @Value("${source-root-url}") String sourceRootUrl
    ) {
        this.scaleImageService = scaleImageService;
        this.awsS3Service = awsS3Service;
        this.sourceRootUrl = sourceRootUrl;
        this.fileValidatorService = fileValidatorService;
    }

    @GetMapping("/show/{predefinedTypeName}/{dummySeoName}")
    public ResponseEntity<Image> getImage(
            @PathVariable String predefinedTypeName,
            @PathVariable("dummySeoName") Optional<String> dummySeoName,
            @RequestParam("reference") String reference
    ){
        try {
            validateProperties(predefinedTypeName, sourceRootUrl + reference);
            return new ResponseEntity<>(scaleImageService.process(predefinedTypeName, reference), HttpStatus.OK);
        } catch (Throwable e) {
            LOGGER.info("Error processing image. Returning 404.", e);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(name = "/flush/{predefinedImageType}")
    @ResponseBody
    public ResponseEntity<Object> flush(
            @PathVariable("predefinedImageType") PredefinedImage predefinedImage,
            @RequestParam(name = "reference", required = true) String relativeImagePath)
    {
        boolean removeImage = awsS3Service.removeImage(predefinedImage, HtmlUtils.htmlUnescape(relativeImagePath));

        if (removeImage) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void validateProperties(String predefinedTypeName, String reference) {
        fileValidatorService.validatePredefinedTypeName(predefinedTypeName);
        fileValidatorService.validateIfFileExists(predefinedTypeName, reference);
        fileValidatorService.validateFileIsImage(reference);
    }
}
