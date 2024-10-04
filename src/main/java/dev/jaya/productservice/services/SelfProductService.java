package dev.jaya.productservice.services;

import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Category;
import dev.jaya.productservice.models.Product;
import dev.jaya.productservice.repositories.CategoryRepository;
import dev.jaya.productservice.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("selfProductService")
public class SelfProductService implements ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private RedisTemplate redisTemplate;

    public SelfProductService(ProductRepository productRepository, CategoryRepository categoryRepository, RedisTemplate redisTemplate) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Product getSingleProduct(Long productId) throws ProductNotFoundException {
        Product productFromRedis = (Product) redisTemplate.opsForHash().get("PRODUCTS", "PRODUCTS_"+productId);
        if(productFromRedis != null) {
            return productFromRedis;
        }

        Optional<Product> p = productRepository.findById(productId);
        if(p.isPresent()) {
            redisTemplate.opsForHash().put("PRODUCTS", "PRODUCTS_"+productId, p.get());
            return p.get();
        }
        throw new ProductNotFoundException("Product not found");
    }

    @Override
    public Page<Product> getProducts(int pageNumber, int pageSize, String fieldName) throws ProductNotFoundException {
        Page<Product> products = productRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(fieldName).ascending()));

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found");
        }

        return products;
    }

    @Override
    public Product createProduct(String title, String description, String category, double price, String image) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(image);
        Category categoryFromDatabase = categoryRepository.findByTitle(category);

        if (categoryFromDatabase == null) {
            Category newCategory = new Category();
            newCategory.setTitle(category);
            categoryFromDatabase = newCategory;
        }
        product.setCategory(categoryFromDatabase);

        Product savedProduct = productRepository.save(product);

        return savedProduct;
    }

    @Override
    public Product deleteSingleProduct(Long productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + productId + " not found."));
        productRepository.delete(product);
        return product;
    }

    @Override
    public List<String> getCategories() {
        return categoryRepository.findAllCategoryTitles();
    }

    @Override
    public Product updateProduct(Long productId, String title, String description, String category, double price, String image) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + productId + " not found."));

        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(image);

        Category categoryFromDatabase = categoryRepository.findByTitle(category);

        if (categoryFromDatabase == null) {
            Category newCategory = new Category();
            newCategory.setTitle(category);
            categoryFromDatabase = categoryRepository.save(newCategory);
        }

        product.setCategory(categoryFromDatabase);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsInCategory(String title) {
        Category category = categoryRepository.findByTitle(title);
        if (category == null) {
            return new ArrayList<>();
        }

        return productRepository.findByCategory(category);
    }
}
