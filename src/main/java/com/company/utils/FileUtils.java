package com.company.utils;

import com.company.model.images.Image;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    /**
     * The defined image path is:
     * ~/<predefined-type-name>/<first-4-chars>/<second-4-chars>/<unique-original-image-file-name>
     * Where:
     *  predefined-type-name: is the name of the definition type, for example thumbnail, or detail-large, etc.
     *  The predefined type holds all the properties for the correct resizing of the image, see the Predefined image types paragraph below.
     *
     *  dummy-seo-name: (optional), is a non-used parameter that could be used to trick search engines into understanding
     *  the image a bit more, for example it could contain the name of the brand and/or product.
     *
     *  reference: is the unique filename and/or relative path to identify the original image on the source domain,
     *  for example "/027/790/13_0277 901000150001_pro_mod_frt_02_1108_1528_1059540.jpg"
     *
     *  predefined-type-name, is the name of the definition type (same as in request url), for example thumbnail.
     *  Note that the original (non-resized) image will automatically get a predefined-type-name of "original"
     *
     *  first-4-chars, if the image unique-original-image-file-name (without the extension) has a length greater than 4,
     *  the first three characters will define the initial sub-directory. Example:
     *  An image with a filename of "abcdefghij.jpg" the first-4-chars are: "abcd"
     *  If the length of the image name is smaller or equal to 4 characters, the image is stored directly in this
     *  directory second-4-chars, if the image unique-original-image-file-name (without the extension) has a length
     *  greater than 8, the second set of 4 characters will define the second sub-directory
     *
     *  Example:
     *  An image with a filename of "abcdefghij.jpg" the second-4-chars are: "efgh"
     *  If the length of the image is greater than 4 but smaller than or equal to 8, the image is stored directly in this directory
     *
     *  unique-original-image-filename, the filename of the original image as was passed into the reference querystring parameter.
     *  Forward slashes will be replaced by underscores ("_").
     */
    public static String getImagePath(String predefinedType, String filename) {

        StringBuilder pathBuilder = new StringBuilder();

        final String extension = FilenameUtils.getExtension(filename);
        String filenameWithoutExtension = FilenameUtils.removeExtension(filename);
        filenameWithoutExtension = filenameWithoutExtension.replace("/","_");

        pathBuilder.append("/").append(predefinedType).append("/");

        if (filenameWithoutExtension.length() >= 4) {
            pathBuilder.append(filenameWithoutExtension, 0, 4).append("/");
        }

        if (filenameWithoutExtension.length() >= 8) {
            pathBuilder.append(filenameWithoutExtension, 4, 8).append("/");
        }

        pathBuilder.append(filenameWithoutExtension);

        return pathBuilder.append(".").append(extension).toString();
    }

    public static File getFileFromImage(Image image, String path){
        File file = new File(path);
        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(image.getImageContent(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static boolean checkIfFileIsAnImage(String filename){
        try {
            java.awt.Image image = ImageIO.read(new File(filename));

            if (image == null) {
                return false;
            }
        } catch(IOException ex) {
            return false;
        }

        return true;
    }

    public static boolean fileExists(String filename){
        File file = new File(filename);
        return file.isFile();
    }
}
