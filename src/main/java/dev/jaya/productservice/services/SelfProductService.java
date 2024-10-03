package dev.jaya.productservice.services;

import dev.jaya.productservice.exceptions.ProductNotFoundException;
import dev.jaya.productservice.models.Category;
import dev.jaya.productservice.models.Product;
import dev.jaya.productservice.repositories.CategoryRepository;
import dev.jaya.productservice.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("selfProductService")
public class SelfProductService implements ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public SelfProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product getSingleProduct(Long productId) throws ProductNotFoundException {
        Optional<Product> p = productRepository.findById(productId);
        if(p.isPresent()) {
            return p.get();
        }
        throw new ProductNotFoundException("Product not found");
    }

    @Override
    public List<Product> getProducts() throws ProductNotFoundException {
        List<Product> products = productRepository.findAll();

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
//            categoryFromDatabase = categoryRepository.save(newCategory);
            categoryFromDatabase = newCategory;
//            category1 = new Category();
//            category1.setTitle(category);
        }

        // if the category was found from DB -> category1 will be having an ID
        // else: category1 won't have any ID
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
}
