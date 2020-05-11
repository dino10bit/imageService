package com.company.model.images.predefinedTypes;

public class PredefinedImageFactory {

    public static PredefinedImage getPredefinedImage(String type){
        if("thumbnail".equals(type)){
            return new Thumbnail();
        }
        if("detailLarge".equals(type)){
            return new DetailLarge();
        }

        return new PredefinedImage();
    }
}
