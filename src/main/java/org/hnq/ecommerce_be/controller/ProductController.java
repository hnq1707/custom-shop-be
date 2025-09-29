package org.hnq.ecommerce_be.controller;

import jakarta.validation.Valid;
import org.hnq.ecommerce_be.dto.product.ProductRequest;
import org.hnq.ecommerce_be.entity.Product;
import org.hnq.ecommerce_be.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.listAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
}
