package com.company.model.images;

import java.io.InputStream;

public class Image {

    private InputStream imageContent;

    public Image(InputStream imageContent) {
        this.imageContent = imageContent;
    }

    public InputStream getImageContent() {
        return imageContent;
    }

    public void setImageContent(InputStream imageContent) {
        this.imageContent = imageContent;
    }
}
