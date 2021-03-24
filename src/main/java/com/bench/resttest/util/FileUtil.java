package com.bench.resttest.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileUtil {

    public static File loadFileFromPath(String filePath) throws NullPointerException, IOException {
        try (InputStream in = FileUtil.class.getResourceAsStream(filePath)) {
            File tempFile = File.createTempFile(filePath, ".tmp");

            Files.copy(in,
                    tempFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(in);

            return tempFile;
        } catch (NullPointerException | IOException e) {
            log.error("Failed to load file in path [{}].", filePath, e);
            throw e;
        }
    }

    public static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}