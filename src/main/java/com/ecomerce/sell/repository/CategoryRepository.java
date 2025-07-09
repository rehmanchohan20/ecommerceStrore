package com.ecomerce.sell.repository;

import com.ecomerce.sell.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNullAndActiveTrue();
    List<Category> findByParentAndActiveTrue(Category parent);
}
