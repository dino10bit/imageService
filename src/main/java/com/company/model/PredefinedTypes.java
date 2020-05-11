package com.company.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PredefinedTypes {
    private List<String> imageTypes;

    public PredefinedTypes() {
        imageTypes = new ArrayList<>();
        imageTypes.add("thumbnail");
        imageTypes.add("detail-large");
    }

    public List<String> getImageTypes() {
        return imageTypes;
    }

    public boolean containsImageType(String imageType){
        return imageTypes.contains(imageType);
    }
}
