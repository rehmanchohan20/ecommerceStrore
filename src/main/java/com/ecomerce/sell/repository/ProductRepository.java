package com.ecomerce.sell.repository;

import com.ecomerce.sell.model.Category;
import com.ecomerce.sell.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product getProductById(Long id);

    List<Product> findByCategory(Category category, Pageable pageable);
    List<Product> findByCategoryIn(List<Category> categories, Pageable pageable);
    Page<Product> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameTerm, String descriptionTerm, Pageable pageable);



}
