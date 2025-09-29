package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.dto.product.ProductRequest;
import org.hnq.ecommerce_be.entity.Product;
import org.hnq.ecommerce_be.repository.ProductRepository;
import org.hnq.ecommerce_be.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    @Transactional
    public Product create(ProductRequest request) {
        String designUrl = request.getDesignUrl();

            try {
                String[] parts = designUrl.split(",");
                String base64Data = parts.length > 1 ? parts[1] : parts[0];
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                // 🔥 Đọc đường dẫn từ cấu hình
                String uploadDir = "uploads/designs/";
                Files.createDirectories(Paths.get(uploadDir));

                String filename = "design-" + System.currentTimeMillis() + ".png";
                Path filePath = Paths.get(uploadDir + filename);
                Files.write(filePath, imageBytes);

                // Trả URL để frontend có thể truy cập
                designUrl = "/static/designs/" + filename;

            } catch (IOException e) {
                throw new RuntimeException("Failed to save base64 image", e);
            }


        Product p = Product.builder()
                .type(request.getType())
                .size(request.getSize())
                .designUrl(designUrl)
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .build();

        return productRepository.save(p);
    }



    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }
}
