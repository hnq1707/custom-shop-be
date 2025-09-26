package org.hnq.ecommerce_be.repository;

import org.hnq.ecommerce_be.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
