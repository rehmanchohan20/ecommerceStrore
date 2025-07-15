package com.ecomerce.sell.controller;

import com.ecomerce.sell.model.CartItem;
import com.ecomerce.sell.model.Users;
import com.ecomerce.sell.model.Vos.CartItemVo;
import com.ecomerce.sell.service.CartService;
import com.ecomerce.sell.util.AuthUtils;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService; // Assuming you have a CartService to handle cart operations

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems() {
        Users users = Objects.requireNonNull(AuthUtils.getCurrentUser()).getUser(); // Assuming you have a utility to get the current user
        Response response = cartService.getAllItemsForUser(users);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCartItem(@RequestBody CartItemVo cartItem) {
        Response response = cartService.add(cartItem);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/delete/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        Response response = cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
