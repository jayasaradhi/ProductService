package dev.jaya.productservice.repositories;

import dev.jaya.productservice.models.Category;
import dev.jaya.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product p);

    @Override
    Page<Product> findAll(Pageable pageable);

    Product findByIdIs(Long id);

    void delete(Product product);

    List<Product> findByCategory(Category category);


}
