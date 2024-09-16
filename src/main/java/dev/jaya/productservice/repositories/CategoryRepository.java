package dev.jaya.productservice.repositories;

import dev.jaya.productservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByTitle(String title);

    Category save(Category category);

    @Query("SELECT c.title FROM Category c")
    List<String> findAllCategoryTitles();
}
