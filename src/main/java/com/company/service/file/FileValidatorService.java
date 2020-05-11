package com.company.service.file;

import com.company.exception.NotFoundException;
import com.company.model.PredefinedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.company.utils.FileUtils.checkIfFileIsAnImage;
import static com.company.utils.FileUtils.fileExists;

@Service
public class FileValidatorService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public void validatePredefinedTypeName(String predefinedTypeName) {

        PredefinedTypes predefinedTypes = new PredefinedTypes();
        try {
            if (!predefinedTypes.containsImageType(predefinedTypeName)) {
                throw new NotFoundException("The predefined type " + predefinedTypeName + " is not defined");
            }
        } catch (NotFoundException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void validateIfFileExists(String predefinedTypeName, String reference) {
        try {
            if (!fileExists(reference)) {
                throw new NotFoundException(predefinedTypeName + " is not a file");
            }
        } catch (NotFoundException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void validateFileIsImage(String reference) {
        try {
            if (!checkIfFileIsAnImage(reference)) {
                throw new NotFoundException("The file " + reference + " is not an image");
            }
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
