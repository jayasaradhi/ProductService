package dev.jaya.productservice.controllers;

import dev.jaya.productservice.dtos.CreateProductRequestDto;
import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Product;
import dev.jaya.productservice.services.FakeStoreProductService;
import dev.jaya.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private ProductService productService;

    public ProductController(@Qualifier("selfProductService") ProductService productService) {
        this.productService = productService;
    }
    @PostMapping("/products")
    public Product createProduct(@RequestBody CreateProductRequestDto request){
        return productService.createProduct(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getPrice(),
                request.getImage()
        );
    }

    @GetMapping("/products/{id}")
    public Product getProductDetails(@PathVariable("id") Long productId) throws ProductNotFoundException {
        return productService.getSingleProduct(productId);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() throws ProductNotFoundException{
        return productService.getProducts();
    }

    @GetMapping("/products/categories")
    public List<String> getAllCategories(){
        return productService.getCategories();
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable("id") Long productId,
                                 @RequestBody CreateProductRequestDto request) throws ProductNotFoundException {
        return productService.updateProduct(
                productId,
                request.getTitle(),
                request.getDescription(),
                request.getCategory(), 
                request.getPrice(),
                request.getImage()
        );

    }

    @DeleteMapping("/products/{id}")
    public Product deleteProduct(@PathVariable("id") Long productId) throws ProductNotFoundException {
        return productService.deleteSingleProduct(productId);
    }
}
