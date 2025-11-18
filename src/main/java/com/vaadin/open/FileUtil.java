package com.vaadin.open;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static String readFile(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            return toString(in);
        }
    }

    public static String read(InputStream inputStream) throws IOException {
        return toString(inputStream);
    }

    public static void copy(InputStream in, FileOutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    private static String toString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                result.append(buffer, 0, charsRead);
            }
        }
        return result.toString();
    }

}
