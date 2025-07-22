package com.ecomerce.sell.service;

import com.ecomerce.sell.model.Users;
import com.ecomerce.sell.model.Vos.CartItemVo;
import com.ecomerce.sell.util.response.Response;

public interface CartService {
    Response getAllItemsForUser(Users users);
    Response add(CartItemVo cartItemVo);
    Response deleteCartItem(Long cartItemId);
    Response placeOrder(Long userId);
}
