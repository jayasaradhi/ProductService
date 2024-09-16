package dev.jaya.productservice.services;

import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Product;

import java.util.List;

public interface ProductService {
    Product getSingleProduct(Long productId) throws ProductNotFoundException;
    List<Product> getProducts();
    Product createProduct(String title,
                          String description,
                          String category,
                          double price,
                          String image);
    Product deleteSingleProduct(Long productId) throws ProductNotFoundException;
    List<String> getCategories();
    Product updateProduct(Long productId,
                          String title,
                          String description,
                          String category,
                          double price,
                          String image) throws ProductNotFoundException;
}
