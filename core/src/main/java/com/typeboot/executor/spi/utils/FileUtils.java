package com.typeboot.executor.spi.utils;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {
    public static String readContent(String fileName) {
        try {
            return Files.readString(Paths.get(fileName));
        } catch (Exception ex) {
            throw new RuntimeException(String.format("error reading file [%s]", fileName));
        }
    }
}
