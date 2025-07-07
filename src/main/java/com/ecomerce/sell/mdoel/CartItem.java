package com.ecomerce.sell.mdoel;

import com.ecomerce.sell.mdoel.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @ManyToOne
    private Users user;

    @ManyToOne
    private Product product;

    private int quantity;
}
