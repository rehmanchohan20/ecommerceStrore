package com.ecomerce.sell.model.Vos;

import com.ecomerce.sell.model.CartItem;
import lombok.Data;
import lombok.ToString;

@Data
public class CartItemVo {
    private Long productId;
    private Integer quantity;

    public static CartItemVo fromCartItem(CartItem cartItem) {
        CartItemVo cartItemVo = new CartItemVo();
        cartItemVo.setProductId(cartItem.getProduct().getId());
        cartItemVo.setQuantity(cartItem.getQuantity());
        return cartItemVo;
    }
}
