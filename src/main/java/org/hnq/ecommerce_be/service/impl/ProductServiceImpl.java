package org.hnq.ecommerce_be.service.impl;

import org.hnq.ecommerce_be.dto.product.ProductRequest;
import org.hnq.ecommerce_be.entity.Product;
import org.hnq.ecommerce_be.repository.ProductRepository;
import org.hnq.ecommerce_be.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(ProductRequest request) {
        Product p = Product.builder()
                .type(request.getType())
                .size(request.getSize())
                .designUrl(request.getDesignUrl())
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
