package com.github.elections2021.util;

import com.github.elections2021.pipeline.AddTvdIdsToUiks;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class FileUtils {

    public static File getResourceFile(String resourceFileName) {
        // see https://mkyong.com/java/java-read-a-file-from-resources-folder/
        try {
            final URL resource = AddTvdIdsToUiks.class
                .getClassLoader()
                .getResource(resourceFileName);

            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
