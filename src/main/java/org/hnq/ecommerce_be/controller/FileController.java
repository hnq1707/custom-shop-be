package org.hnq.ecommerce_be.controller;

import org.hnq.ecommerce_be.util.FileUploadUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/files", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {

    // Upload base64 image: params base64, optional folder (default designs)
    @PostMapping(path = "/upload-base64")
    public Map<String, String> uploadBase64(@RequestParam("base64") String base64,
                                            @RequestParam(value = "folder", required = false, defaultValue = "designs") String folder) {
        String url = FileUploadUtil.saveBase64Image(base64, folder);
        return Map.of("url", url);
    }

    // Upload multipart image file
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadFile(@RequestPart("file") MultipartFile file,
                                          @RequestPart(value = "folder", required = false) String folder) {
        String target = folder == null || folder.isBlank() ? "designs" : folder;
        String url = FileUploadUtil.saveMultipart(file, target);
        return Map.of("url", url);
    }
}
