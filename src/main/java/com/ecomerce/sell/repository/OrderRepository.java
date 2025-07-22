package com.ecomerce.sell.repository;

import com.ecomerce.sell.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByIdAndOrderStatus(Long id, String orderStatus);
}
