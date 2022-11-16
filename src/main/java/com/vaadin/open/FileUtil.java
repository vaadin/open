package com.vaadin.open;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class FileUtil {

    public static String readFile(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        }
    }

    public static String read(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    public static void copy(InputStream in, FileOutputStream out) throws IOException {
        IOUtils.copy(in, out);
    }

}
