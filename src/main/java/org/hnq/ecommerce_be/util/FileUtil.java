package org.hnq.ecommerce_be.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUtil {

    private final Path designsDir;

    public FileUtil(@Value("${app.uploads.designs-dir}") String designsDir) throws IOException {
        this.designsDir = Paths.get(designsDir).toAbsolutePath().normalize();
        Files.createDirectories(this.designsDir);
    }

    public String saveDesignPng(byte[] pngBytes) throws IOException {
        String fileName = UUID.randomUUID() + ".png";
        Path target = designsDir.resolve(fileName);
        Files.write(target, pngBytes);
        return "/static/designs/" + fileName;
    }
}
