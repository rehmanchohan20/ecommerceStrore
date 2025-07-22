package com.ecomerce.sell.model.Vos;

import com.ecomerce.sell.model.CartItem;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
public class CartItemVo {
    private Long productId;
    private Integer quantity;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;

    public static CartItemVo fromCartItem(CartItem cartItem) {
        CartItemVo cartItemVo = new CartItemVo();
        if(cartItem.getProduct() != null) {
            cartItemVo.setProductId(cartItem.getProduct().getId());
            cartItemVo.setProductName(cartItem.getProduct().getName());
            cartItemVo.setProductDescription(cartItem.getProduct().getDescription());
            cartItemVo.setProductPrice(cartItem.getProduct().getPrice());
        }
        cartItemVo.setQuantity(cartItem.getQuantity());
        return cartItemVo;
    }
}
