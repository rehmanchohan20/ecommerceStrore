package com.ecomerce.sell.repository;

import com.ecomerce.sell.model.CartItem;
import com.ecomerce.sell.model.Product;
import com.ecomerce.sell.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(Users user);
    CartItem findByUserAndProduct(Users user, Product product);

    CartItem getCartItemById(Long id);
}
