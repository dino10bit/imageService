package com.company.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AwsClientUtilsTest {

    @Test
    public void getDirectoryPathOfImage__filename_with_less_than_4_characters(){
        final String imagePath = "abc.jpg";

        String s3PathOfImage = FileUtils.getImagePath("test", imagePath);

        assertThat(s3PathOfImage).isEqualTo("/test/abc.jpg");
    }

    @Test
    public void getDirectoryPathOfImage__filename_with_more_than_4_characters_and_less_than_8(){
        final String imagePath = "abcdefg.jpg";

        String s3PathOfImage = FileUtils.getImagePath("test", imagePath);

        assertThat(s3PathOfImage).isEqualTo("/test/abcd/abcdefg.jpg");
    }

    @Test
    public void getDirectoryPathOfImage__filename_with_more_than_8_characters(){
        final String imagePath = "abcdefgh.jpg";

        String s3PathOfImage = FileUtils.getImagePath("test", imagePath);

        assertThat(s3PathOfImage).isEqualTo("/test/abcd/efgh/abcdefgh.jpg");
    }

    @Test
    public void getDirectoryPathOfImage__filename_with_directory_name_included(){
        final String imagePath = "/mydirectory/pictures/abcdefgh.jpg";

        String s3PathOfImage = FileUtils.getImagePath("test", imagePath);

        assertThat(s3PathOfImage).isEqualTo("/test/_myd/irec/_mydirectory_pictures_abcdefgh.jpg");
    }

}