package dev.jaya.productservice.repositories;

import dev.jaya.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product p);

    @Override
    List<Product> findAll();

    Product findByIdIs(Long id);

    void delete(Product product);

}
