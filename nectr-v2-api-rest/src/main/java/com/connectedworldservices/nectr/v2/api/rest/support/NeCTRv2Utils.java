package com.connectedworldservices.nectr.v2.api.rest.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.jgit.util.FileUtils;

@Slf4j
public final class NeCTRv2Utils {

    private NeCTRv2Utils() {
        //utility class can't be instantiated
    }

    public static File createTempDirectory(String name) throws IOException {
        File directory = Files.createTempDirectory(name).toFile();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                deleteDirectory(directory);
            }
        });
        return directory;
    }

    public static void deleteDirectory(File directory) {
        try {
            if (directory.exists()) {
                FileUtils.delete(directory, FileUtils.RECURSIVE);
            }
        } catch (IOException e) {
            log.warn("Failed to delete temporary directory on exit: " + e);
        }
    }

    public static Object unquote(Object value) {
        if (value == null || !(value instanceof String)) {
            return value;
        }

        String str = (String) value;

        if (isDoubleQuoted(str) || isSingleQuoted(str)) {
            str = str.substring(1, str.length() - 1);
        }

        return str;
    }

    public static boolean isSingleQuoted(String value) {
        if (value == null) {
            return false;
        }

        return value.startsWith("'") && value.endsWith("'");
    }

    public static boolean isDoubleQuoted(String value) {
        if (value == null) {
            return false;
        }

        return value.startsWith("\"") && value.endsWith("\"");
    }
}
