package org.hnq.ecommerce_be.service;

import org.hnq.ecommerce_be.dto.product.ProductRequest;
import org.hnq.ecommerce_be.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(ProductRequest request);
    List<Product> listAll();
}
