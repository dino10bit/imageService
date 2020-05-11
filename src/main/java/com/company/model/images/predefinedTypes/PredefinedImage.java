package com.company.model.images.predefinedTypes;

import com.company.model.images.ScaleType;
import com.company.model.images.ImageType;

public class PredefinedImage {
    private int height;
    private int width;
    private int quality;
    private ScaleType scaleType;
    private int fillColor;
    private ImageType type;

    public PredefinedImage() {
    }

    public PredefinedImage(int height, int width, int quality, ScaleType scaleType, int fillColor, ImageType type) {
        this.height = height;
        this.width = width;
        this.quality = quality;
        this.scaleType = scaleType;
        this.fillColor = fillColor;
        this.type = type;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        if (fillColor.charAt(0) == '#') {
            fillColor = fillColor.substring(1);
        }
        this.fillColor = Integer.parseInt(fillColor, 16);
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }
}
