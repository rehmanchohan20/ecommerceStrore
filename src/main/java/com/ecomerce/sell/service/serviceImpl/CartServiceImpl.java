package com.ecomerce.sell.service.serviceImpl;

import com.ecomerce.sell.excepotions.DAOResponse;
import com.ecomerce.sell.model.CartItem;
import com.ecomerce.sell.model.Product;
import com.ecomerce.sell.model.Users;
import com.ecomerce.sell.model.Vos.CartItemVo;
import com.ecomerce.sell.model.Vos.UsersVo;
import com.ecomerce.sell.repository.CartRepository;
import com.ecomerce.sell.repository.OrderRepository;
import com.ecomerce.sell.repository.ProductRepository;
import com.ecomerce.sell.repository.UserRepository;
import com.ecomerce.sell.service.CartService;
import com.ecomerce.sell.util.AuthUtils;
import com.ecomerce.sell.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private static final Logger logger = Logger.getLogger(CartServiceImpl.class.getName());

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Response getAllItemsForUser(Users user) {
        Response response = new Response();
        try{
            List<CartItem> cartItems = cartRepository.findByUser(user);
            if(cartItems != null && !cartItems.isEmpty()) {
                List<CartItemVo> cartItemVos = cartItems.stream()
                                .map(CartItemVo::fromCartItem)
                                .collect(Collectors.toList());
                response.setResponse(DAOResponse.SUCCESS);
                response.setData("cartItems", cartItemVos);
            } else {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("No items found in the cart for the user.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error fetching cart items: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response add(CartItemVo cartItemVo){
        Response response = new Response();
        Long currentUSerID = AuthUtils.getCurrentUserId();
        try{
            if(cartItemVo == null || cartItemVo.getProductId() == null || cartItemVo.getQuantity() <= 0) {
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("Invalid cart item data.");
                return response;
            }

            Product product = productRepository.getProductById(cartItemVo.getProductId());
            if (product == null) {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("Product not found.");
                return response;
            }

            Users user = userRepository.findByUsername(Objects.requireNonNull(AuthUtils.getCurrentUser()).getUsername());
            if (user == null) {
                response.setResponse(DAOResponse.INVALID_REQUEST);
                response.setMessage("User not found.");
                return response;
            }
            CartItem existingCartItem = cartRepository.findByUserAndProduct(user, product);
            if (existingCartItem != null) {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemVo.getQuantity());
                existingCartItem.setUpdatedBy(String.valueOf(currentUSerID));
                cartRepository.save(existingCartItem);
                response.setResponse(DAOResponse.SUCCESS);
                response.setMessage("Cart item updated successfully.");
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setUser(user);
                newCartItem.setCreatedBy(String.valueOf(currentUSerID));
                newCartItem.setProduct(product);
                newCartItem.setQuantity(cartItemVo.getQuantity());
                cartRepository.save(newCartItem);
                response.setResponse(DAOResponse.SUCCESS);
                response.setMessage("Cart item added successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error adding item to cart: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteCartItem(Long cartItemId) {
        Response response = new Response();
        try {
            CartItem optionalCartItem = cartRepository.getCartItemById(cartItemId);
            if (optionalCartItem != null) {
                cartRepository.delete(optionalCartItem);
                response.setResponse(DAOResponse.SUCCESS);
                response.setMessage("Cart item deleted successfully.");
            } else {
                response.setResponse(DAOResponse.NO_DATA_FOUND);
                response.setMessage("Cart item not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Error deleting cart item: " + e.getMessage());
        }
        return response;
    }
}

