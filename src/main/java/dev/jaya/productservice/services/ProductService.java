package dev.jaya.productservice.services;

import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Category;
import dev.jaya.productservice.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product getSingleProduct(Long productId) throws ProductNotFoundException;
    Page<Product> getProducts(int pageNumber, int pageSize, String filedName) throws ProductNotFoundException;
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
    List<Product> getProductsInCategory(String title);
}
