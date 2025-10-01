package org.hnq.ecommerce_be.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
public class FileUploadUtil {

    private static final String DESIGN_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "designs").toString();

    public static String saveBase64Image(String base64, String folder) {
        try {
            if (base64 == null || base64.isBlank()) {
                throw new IllegalArgumentException("base64 is required");
            }
            String[] parts = base64.split(",");
            String meta = parts[0]; // data:image/png;base64
            String data = parts.length > 1 ? parts[1] : parts[0];

            String ext = meta.contains("png") ? "png" : (meta.contains("webp") ? "webp" : "jpg");
            byte[] decoded = Base64.getDecoder().decode(data);

            String fileName = UUID.randomUUID() + "." + ext;
            String targetDir = resolveDir(folder);
            Path uploadPath = Paths.get(targetDir, fileName);
            Files.createDirectories(uploadPath.getParent());
            Files.write(uploadPath, decoded);

            return publicUrl(folder, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public static String saveMultipart(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf('.') + 1) : "png";
        String fileName = UUID.randomUUID() + "." + ext;

        String targetDir = Paths.get(System.getProperty("user.dir"), "uploads", "designs").toString(); // absolute path
        Path uploadPath = Paths.get(targetDir, fileName);

        try {
            Files.createDirectories(uploadPath.getParent());
            file.transferTo(uploadPath.toFile());
            return "/static/designs/" + fileName; // public URL
        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }


    private static String resolveDir(String folder) {
        // Currently only designs are public; can extend later
        return DESIGN_DIR; // ignore folder param for now to keep behavior consistent
    }

    private static String publicUrl(String folder, String fileName) {
        return "/static/designs/" + fileName;
    }
}
