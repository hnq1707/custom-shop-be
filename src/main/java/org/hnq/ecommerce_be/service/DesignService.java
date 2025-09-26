package org.hnq.ecommerce_be.service;

import org.springframework.web.multipart.MultipartFile;

public interface DesignService {
    String generateDesign(String vietnameseDescription, MultipartFile imageSample);
    String generateDesign(String vietnameseDescription);
}
