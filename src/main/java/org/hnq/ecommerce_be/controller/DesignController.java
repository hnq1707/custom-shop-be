package org.hnq.ecommerce_be.controller;

import jakarta.validation.Valid;
import org.hnq.ecommerce_be.dto.design.DesignGenerateRequest;
import org.hnq.ecommerce_be.dto.design.DesignResponse;
import org.hnq.ecommerce_be.service.DesignService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/designs", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class DesignController {

    private final DesignService designService;

    public DesignController(DesignService designService) {
        this.designService = designService;
    }

    // JSON body: { "description": "..." }
    @PostMapping(path = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DesignResponse generateJson(@Valid @RequestBody DesignGenerateRequest request) {
        String url = designService.generateDesign(request.getDescription());
        return DesignResponse.builder().imageUrl(url).build();
    }

    // Multipart: description + image file
    @PostMapping(path = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DesignResponse generateMultipart(@RequestPart("description") String description,
                                            @RequestPart(value = "image", required = false) MultipartFile image) {
        if (description == null || description.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "description is required");
        }
        String url = designService.generateDesign(description, image);
        return DesignResponse.builder().imageUrl(url).build();
    }
}
