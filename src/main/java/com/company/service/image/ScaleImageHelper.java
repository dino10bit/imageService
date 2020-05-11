package com.company.service.image;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import com.company.model.images.predefinedTypes.PredefinedImage;
import com.company.model.images.ImageType;
import com.company.model.images.Image;

public class ScaleImageHelper {
    public InputStream process(
            Image image,
            PredefinedImage predefinedImageType
    ) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getImageContent());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage resizedImage = scaleImage(predefinedImageType, bufferedImage);

        if (predefinedImageType.getType() == ImageType.JPG) {
            compressJpeg(resizedImage, outputStream, predefinedImageType.getQuality());
        } else {
            ImageIO.write(resizedImage, predefinedImageType.getType().getType(), outputStream);
        }

        outputStream.flush();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void compressJpeg(
            BufferedImage sourceImage,
            ByteArrayOutputStream outputStream,
            int quality
    ) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality / 100f);

        writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        writer.write(null, new IIOImage(sourceImage, null, null), param);
        writer.dispose();
    }

    private BufferedImage scaleImage(
            PredefinedImage predefinedImageType,
            BufferedImage bufferedImage
    ) {
        int imageWidth = predefinedImageType.getWidth();
        int imageHeight = predefinedImageType.getHeight();
        int imageFillColor = predefinedImageType.getFillColor();

        BufferedImage result = new BufferedImage(imageWidth, imageHeight, bufferedImage.getType());
        Graphics2D graphics2D = result.createGraphics();

        switch (predefinedImageType.getScaleType()) {
            case FILL:
                fillImage(bufferedImage, graphics2D, imageHeight, imageWidth, imageFillColor);
                break;
            case CROP:
                cropImage(bufferedImage, graphics2D, imageHeight, imageWidth);
                break;
            default:
                skewImage(bufferedImage, graphics2D, imageHeight, imageWidth);
                break;
        }

        graphics2D.dispose();

        return result;
    }

    private void fillImage(
            BufferedImage bufferedImage,
            Graphics2D graphics2D,
            int height,
            int width,
            int color
    ) {
        int bufferedImageWidth = bufferedImage.getWidth();
        int bufferedImageHeight = bufferedImage.getHeight();
        int newWidth = bufferedImageWidth;
        int newHeight = bufferedImageHeight;

        if (newHeight > height) {
            newHeight = height;
            newWidth = (newHeight * bufferedImageWidth) / bufferedImageHeight;
        }

        if (bufferedImageWidth > width) {
            newWidth = width;
            newHeight = (newWidth * bufferedImageHeight) / bufferedImageHeight;
        }

        int startX = width / 2 - newWidth / 2;
        int startY = height / 2 - newHeight / 2;

        graphics2D.setColor(new Color(color));
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.drawImage(bufferedImage, startX, startY, startX + newWidth, startY + newHeight, 0,
                0, bufferedImageWidth, bufferedImageHeight, null);
    }

    private void cropImage(
            BufferedImage bufferedImage,
            Graphics2D graphics2D,
            int height,
            int width
    ) {
        int startX;
        int startY;
        int newHeight;
        int newWidth;
        float heightRatio = (float) bufferedImage.getHeight() / height;
        float widthRatio = (float) bufferedImage.getWidth() / width;
        float targetAspectRatio = (float) width / height;

        if (widthRatio > heightRatio) {
            newWidth = Math.round(bufferedImage.getHeight() * targetAspectRatio);
            newHeight = bufferedImage.getHeight();
            startX = bufferedImage.getWidth() / 2 - newWidth / 2;
            startY = 0;
        } else {
            newWidth = bufferedImage.getWidth();
            newHeight = Math.round(bufferedImage.getWidth() / targetAspectRatio);
            startY = bufferedImage.getHeight() / 2 - newHeight / 2;
            startX = 0;
        }

        graphics2D.drawImage(bufferedImage, 0, 0, width, height, startX, startY, startX + newWidth,
                startY + newHeight, null);
    }

    private void skewImage(BufferedImage sourceImage, Graphics2D graphics2D, int height, int width) {
        graphics2D.drawImage(sourceImage, 0, 0, width, height, null);
    }
}
